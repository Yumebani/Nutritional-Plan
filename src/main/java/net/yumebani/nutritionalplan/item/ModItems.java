package net.yumebani.nutritionalplan.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.yumebani.nutritionalplan.NutritionalPlanMod;

public class ModItems {

    public static final DeferredRegister<Item> ItemsRegister =
            DeferredRegister.create(Registries.ITEM, NutritionalPlanMod.MODID);

    public static void register(IEventBus eventBus){
        ItemsRegister.register(eventBus);
    }

    public static final DeferredHolder<Item, Item> NUTRITION_PLANNER = ItemsRegister.register(
            "nutrition_planner", () -> new NutritionPlanner(new Item.Properties().stacksTo(1)));
}
