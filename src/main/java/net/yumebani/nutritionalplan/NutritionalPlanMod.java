package net.yumebani.nutritionalplan;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.yumebani.nutritionalplan.item.ModItems;

@Mod(NutritionalPlanMod.MODID)
public class NutritionalPlanMod {

    public static final String MODID = "nutritionalplan";

    public NutritionalPlanMod(IEventBus modEventBus, ModContainer modContainer) {

        modContainer.registerConfig(ModConfig.Type.CLIENT, ModClientConfigValues.CONFIG_SPEC);
        modContainer.registerConfig(ModConfig.Type.SERVER, ModConfigValues.CONFIG_SPEC);

        ModItems.register(modEventBus);
    }
}
