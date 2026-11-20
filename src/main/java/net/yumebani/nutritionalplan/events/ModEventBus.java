package net.yumebani.nutritionalplan.events;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.yumebani.nutritionalplan.ModConfigValues;
import net.yumebani.nutritionalplan.NutritionalPlanMod;
import net.yumebani.nutritionalplan.data.ClientBoundFoodDataPacket;
import net.yumebani.nutritionalplan.data.NutritionDataClient;
import net.yumebani.nutritionalplan.data.NutritionSavedData;
import net.yumebani.nutritionalplan.data.NutritionUpgrades;
import net.yumebani.nutritionalplan.item.ModItems;

import java.util.UUID;

@EventBusSubscriber(modid = NutritionalPlanMod.MODID)
public class ModEventBus {

    @SubscribeEvent
    public static void onItemUseFinish(final LivingEntityUseItemEvent.Finish event) {

        LivingEntity entity = event.getEntity();
        ItemStack foodStack = event.getItem();
        if (!foodStack.has(DataComponents.FOOD)) {
            return;
        }

        if (entity.level().isClientSide() && entity instanceof Player) {
            NutritionDataClient.getInstance().foodEaten(foodStack.getItem());
        } else if (entity instanceof Player player){
            NutritionSavedData.computeIfAbsent(player.getServer()).foodEaten(player.getUUID(), foodStack.getItem());
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(final PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            NutritionSavedData data = NutritionSavedData.computeIfAbsent(event.getEntity().getServer());
            if (data.hasNewPlayer(serverPlayer.getUUID())){
                data.registerNewPlayer(serverPlayer.getUUID());
            }
            PacketDistributor.sendToPlayer(serverPlayer, data.createPacket(serverPlayer.getUUID()));
            data.applyNewModifiers(serverPlayer.getServer(), serverPlayer.getUUID());
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(final PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            NutritionSavedData data = NutritionSavedData.computeIfAbsent(event.getEntity().getServer());
            data.applyNewModifiers(serverPlayer.getServer(), serverPlayer.getUUID());
        }
    }

    @SubscribeEvent
    public static void onIncomingDamage(final LivingIncomingDamageEvent event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }

        NutritionSavedData data = NutritionSavedData.computeIfAbsent(event.getEntity().getServer());

        if (event.getEntity() instanceof Player player){
            if (data.getNutritionUpgrade(player.getUUID(), NutritionUpgrades.RESISTANCE) > 0) {
                event.setAmount(Math.max(0.0f, event.getAmount() * (1 - (float)(ModConfigValues.CONFIG.resistanceUpgradeReductionPerLevel.get() * data.getNutritionUpgrade(player.getUUID(), NutritionUpgrades.RESISTANCE)))));
            }
        } else {
            if (event.getSource().getEntity() instanceof Player player) {
                if (data.getNutritionUpgrade(player.getUUID(), NutritionUpgrades.STRENGTH) > 0) {
                    event.setAmount(Math.max(0.0f, event.getAmount() * (1 + (float)(data.getNutritionUpgrade(player.getUUID(), NutritionUpgrades.STRENGTH) * ModConfigValues.CONFIG.strengthUpgradeDmgPerLevel.get()))));
                }
            }
        }
    }

    @SubscribeEvent
    public static void onServerTick(final ServerTickEvent.Post event) {

        NutritionSavedData data = NutritionSavedData.computeIfAbsent(event.getServer());
        data.tick();

        for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()){
            if (!player.isAlive() || player.getHealth() >= player.getMaxHealth() || data.getNutritionUpgrade(player.getUUID(), NutritionUpgrades.REGENERATION) <= 0) {
                continue;
            }

            if (event.getServer().getTickCount() % Math.max(0, ModConfigValues.CONFIG.regenUpgradeBaseInterval.get() - (ModConfigValues.CONFIG.regenUpgradeIntervalDecrease.get() * data.getNutritionUpgrade(player.getUUID(), NutritionUpgrades.REGENERATION))) == 0){
                player.heal(1f);
            }
        }
    }

    @SubscribeEvent
    public static void onBuildCreativeTabContents(final BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS) {
            event.accept(ModItems.NUTRITION_PLANNER.get());
        }
    }

    @SubscribeEvent
    public static void onRegisterCommands(final RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("np")
                        .requires(source -> source.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .then(Commands.literal("reset")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(context -> {
                                            ServerPlayer player = EntityArgument.getPlayer(context, "player");
                                            NutritionSavedData data = NutritionSavedData.computeIfAbsent(context.getSource().getServer());
                                            if (data.tryReset(player)){
                                                data.updatePlayerData(player);
                                                context.getSource().sendSuccess(() -> Component.literal("Cleared data for " + player.getName().getString()), false);
                                                return 1;
                                            } else {
                                                context.getSource().sendFailure(Component.literal("No player with that name"));
                                                return -1;
                                            }
                                        })
                        ))
                        .then(Commands.literal("cooldown")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .then(Commands.literal("reset")
                                                .executes(context -> {
                                                    ServerPlayer player = EntityArgument.getPlayer(context, "player");
                                                    NutritionSavedData data = NutritionSavedData.computeIfAbsent(context.getSource().getServer());
                                                    data.resetCooldown(player.getUUID());
                                                    data.updatePlayerData(player);
                                                    return 1;
                                                })
                                )))
                        .then(Commands.literal("level")
                                    .then(Commands.argument("player", EntityArgument.player())
                                        .then(Commands.literal("add")
                                                .then(Commands.argument("amount", IntegerArgumentType.integer(0, 100))
                                                .executes(context -> {
                                                    ServerPlayer player = EntityArgument.getPlayer(context, "player");
                                                    int amount = IntegerArgumentType.getInteger(context, "amount");
                                                    NutritionSavedData data = NutritionSavedData.computeIfAbsent(context.getSource().getServer());
                                                    data.levelUp(player.getUUID(), amount);
                                                    data.updatePlayerData(player);
                                                    return 1;
                                                })
                                            )
                                        ).then(Commands.literal("sub")
                                                    .then(Commands.argument("amount", IntegerArgumentType.integer(0, 100))
                                                            .executes(context -> {
                                                                ServerPlayer player = EntityArgument.getPlayer(context, "player");
                                                                int amount = IntegerArgumentType.getInteger(context, "amount");
                                                                NutritionSavedData data = NutritionSavedData.computeIfAbsent(context.getSource().getServer());
                                                                data.delevelUp(player.getUUID(), amount);
                                                                data.updatePlayerData(player);
                                                                return 1;
                                                            })
                                                    )
                                            )
                                    )
                        ).then(Commands.literal("exp")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .then(Commands.literal("add")
                                                .then(Commands.argument("amount", IntegerArgumentType.integer(0, 10000))
                                                        .executes(context -> {
                                                            ServerPlayer player = EntityArgument.getPlayer(context, "player");
                                                            int amount = IntegerArgumentType.getInteger(context, "amount");
                                                            NutritionSavedData data = NutritionSavedData.computeIfAbsent(context.getSource().getServer());
                                                            data.addExp(player.getUUID(), amount);
                                                            data.updatePlayerData(player);
                                                            return 1;
                                                        })
                                                )
                                        ).then(Commands.literal("sub")
                                                .then(Commands.argument("amount", IntegerArgumentType.integer(0, 10000))
                                                        .executes(context -> {
                                                            ServerPlayer player = EntityArgument.getPlayer(context, "player");
                                                            int amount = IntegerArgumentType.getInteger(context, "amount");
                                                            NutritionSavedData data = NutritionSavedData.computeIfAbsent(context.getSource().getServer());
                                                            data.subExp(player.getUUID(), amount);
                                                            data.updatePlayerData(player);
                                                            return 1;
                                                        })
                                                )
                                        )
                                ))
                                .then(Commands.literal("points")
                                        .then(Commands.argument("player", EntityArgument.player())
                                                .then(Commands.literal("add")
                                                        .then(Commands.argument("amount", IntegerArgumentType.integer(0, 10000))
                                                                .executes(context -> {
                                                                    ServerPlayer player = EntityArgument.getPlayer(context, "player");
                                                                    int amount = IntegerArgumentType.getInteger(context, "amount");
                                                                    NutritionSavedData data = NutritionSavedData.computeIfAbsent(context.getSource().getServer());
                                                                    data.addPoints(player.getUUID(), amount);
                                                                    data.updatePlayerData(player);
                                                                    return 1;
                                                                })
                                                        )
                                                ).then(Commands.literal("sub")
                                                        .then(Commands.argument("amount", IntegerArgumentType.integer(0, 10000))
                                                                .executes(context -> {
                                                                    ServerPlayer player = EntityArgument.getPlayer(context, "player");
                                                                    int amount = IntegerArgumentType.getInteger(context, "amount");
                                                                    NutritionSavedData data = NutritionSavedData.computeIfAbsent(context.getSource().getServer());
                                                                    data.subPoints(player.getUUID(), amount);
                                                                    data.updatePlayerData(player);
                                                                    return 1;
                                                                })
                                                        )
                                                )
                                        )
                                )

        );
    }
}
