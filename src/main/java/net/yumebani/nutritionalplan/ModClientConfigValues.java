package net.yumebani.nutritionalplan;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ModClientConfigValues {

    public static final ModClientConfigValues CONFIG;
    public static final ModConfigSpec CONFIG_SPEC;

    public final ModConfigSpec.BooleanValue enableFoodTooltips;

    static {
        Pair<ModClientConfigValues, ModConfigSpec> pair =
                new ModConfigSpec.Builder().configure(ModClientConfigValues::new);
        CONFIG = pair.getLeft();
        CONFIG_SPEC = pair.getRight();
    }

    private ModClientConfigValues(ModConfigSpec.Builder builder) {

        builder.comment("General / client display settings").push("general");

        enableFoodTooltips = builder
                .comment("Show exp gain for food in tooltip")
                .define("enable_food_tooltips", true);

        builder.pop();
    }
}
