package net.yumebani.nutritionalplan.data;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.yumebani.nutritionalplan.NutritionalPlanMod;

import java.util.HashMap;
import java.util.Map;

public record ClientBoundFoodDataPacket(
        Map<String, Integer> eatenFoods,
        Map<NutritionUpgrades, Integer> nutritionUpgrades,
        int exp,
        int level,
        int points,
        int cooldown
) implements CustomPacketPayload {

    public static final Type<ClientBoundFoodDataPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(NutritionalPlanMod.MODID, "client_bound_food_data_packet"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientBoundFoodDataPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, ByteBufCodecs.INT), ClientBoundFoodDataPacket::eatenFoods,
            ByteBufCodecs.map(HashMap::new, NeoForgeStreamCodecs.enumCodec(NutritionUpgrades.class), ByteBufCodecs.INT), ClientBoundFoodDataPacket::nutritionUpgrades,
            ByteBufCodecs.INT, ClientBoundFoodDataPacket::exp,
            ByteBufCodecs.INT, ClientBoundFoodDataPacket::level,
            ByteBufCodecs.INT, ClientBoundFoodDataPacket::points,
            ByteBufCodecs.INT, ClientBoundFoodDataPacket::cooldown,
            ClientBoundFoodDataPacket::new
    );

    public static void handle(ClientBoundFoodDataPacket packet, IPayloadContext context){
        NutritionDataClient data = NutritionDataClient.getInstance();
        data.loadClientBoundData(packet.eatenFoods(), packet.nutritionUpgrades(), packet.exp(), packet.level(), packet.points(), packet.cooldown());
    }
}
