package net.yumebani.nutritionalplan.data;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.yumebani.nutritionalplan.NutritionalPlanMod;

@EventBusSubscriber(modid = NutritionalPlanMod.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModPacketHandler {

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1.0.0");

        registrar.playToServer(
                ServerBoundFoodDataPacket.TYPE,
                ServerBoundFoodDataPacket.STREAM_CODEC,
                ServerBoundFoodDataPacket::handle
        );

        registrar.playToClient(
                ClientBoundFoodDataPacket.TYPE,
                ClientBoundFoodDataPacket.STREAM_CODEC,
                ClientBoundFoodDataPacket::handle
        );
    }
}
