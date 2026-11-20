package net.yumebani.nutritionalplan.data;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.yumebani.nutritionalplan.NutritionalPlanMod;

import java.util.Optional;

public record ServerBoundFoodDataPacket(
        Optional<Item> foodItem,
        Optional<NutritionUpgrades> upgrade,
        Boolean spendUpgrade
) implements CustomPacketPayload {

    public static final Type<ServerBoundFoodDataPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(NutritionalPlanMod.MODID, "server_bound_food_data_packet"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerBoundFoodDataPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(ByteBufCodecs.registry(Registries.ITEM)), ServerBoundFoodDataPacket::foodItem,
            ByteBufCodecs.optional(NeoForgeStreamCodecs.enumCodec(NutritionUpgrades.class)), ServerBoundFoodDataPacket::upgrade,
            ByteBufCodecs.BOOL, ServerBoundFoodDataPacket::spendUpgrade,
            ServerBoundFoodDataPacket::new
    );

    public static void handle(ServerBoundFoodDataPacket packet, IPayloadContext context){

        context.enqueueWork(() -> {

            NutritionSavedData data = NutritionSavedData.computeIfAbsent(context.player().getServer());

            if (packet.foodItem().isPresent()){
                data.foodEaten(context.player().getUUID(), packet.foodItem().get());
            }

            if (packet.upgrade().isPresent()){
                if (packet.spendUpgrade()){
                    data.spendPoints(context.player().getServer(), context.player().getUUID(), packet.upgrade().get());
                } else {
                    data.unspendPoints(context.player().getServer(), context.player().getUUID(), packet.upgrade().get());
                }
            }
        });
    }
}
