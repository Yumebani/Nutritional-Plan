package net.yumebani.nutritionalplan.events;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.yumebani.nutritionalplan.ModClientConfigValues;
import net.yumebani.nutritionalplan.NutritionalPlanMod;
import net.yumebani.nutritionalplan.data.NutritionDataClient;

import java.util.List;

@EventBusSubscriber(modid = NutritionalPlanMod.MODID, value = Dist.CLIENT)
public class ClientEventBus {

    @SubscribeEvent
    public static void onItemTooltip(final ItemTooltipEvent event) {
        if (ModClientConfigValues.CONFIG.enableFoodTooltips.get()){

            ItemStack stack = event.getItemStack();
            FoodProperties foodProperties = stack.get(DataComponents.FOOD);
            if (foodProperties == null) {
                return;
            }

            List<Component> tooltip = event.getToolTip();
            if (NutritionDataClient.getInstance().getFoodExp(stack.getItem()) > 0){
                tooltip.add(
                        Component.literal(NutritionDataClient.getInstance().getFoodExp(stack.getItem()) + " nutri-xp can be obtained.")
                                .withStyle(ChatFormatting.GOLD)
                );
            } else {
                tooltip.add(
                        Component.literal("No nutri-xp can be obtained.")
                                .withStyle(ChatFormatting.GRAY)
                );
            }
        }
    }

    @SubscribeEvent
    public static void onClientPlayerTick(final PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (!player.level().isClientSide()) {
            NutritionDataClient.getInstance().tick();
        }
    }
}
