package net.yumebani.nutritionalplan;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ModConfigValues {

    public static final ModConfigValues CONFIG;
    public static final ModConfigSpec CONFIG_SPEC;

    public final ModConfigSpec.IntValue baseLevelExp;
    public final ModConfigSpec.IntValue levelExpRamping;
    public final ModConfigSpec.IntValue pointsPerLevel;
    public final ModConfigSpec.IntValue maxLevel;

    public final ModConfigSpec.DoubleValue foodExpMultiplier;
    public final ModConfigSpec.DoubleValue foodExpReductionPerUsage;

    public final ModConfigSpec.IntValue upgradeCooldown;

    public final ModConfigSpec.IntValue healthUpgradeMaxLevel;
    public final ModConfigSpec.IntValue healthUpgradeBaseCost;
    public final ModConfigSpec.IntValue healthUpgradeRampingCost;
    public final ModConfigSpec.IntValue healthUpgradeHealthPerLevel;

    public final ModConfigSpec.IntValue regenUpgradeMaxLevel;
    public final ModConfigSpec.IntValue regenUpgradeBaseCost;
    public final ModConfigSpec.IntValue regenUpgradeRampingCost;
    public final ModConfigSpec.IntValue regenUpgradeBaseInterval;
    public final ModConfigSpec.IntValue regenUpgradeIntervalDecrease;

    public final ModConfigSpec.IntValue hasteUpgradeMaxLevel;
    public final ModConfigSpec.IntValue hasteUpgradeBaseCost;
    public final ModConfigSpec.IntValue hasteUpgradeRampingCost;
    public final ModConfigSpec.DoubleValue hasteUpgradeSpeedPerLevel;

    public final ModConfigSpec.IntValue speedUpgradeMaxLevel;
    public final ModConfigSpec.IntValue speedUpgradeBaseCost;
    public final ModConfigSpec.IntValue speedUpgradeRampingCost;
    public final ModConfigSpec.DoubleValue speedUpgradeSpeedPerLevel;

    public final ModConfigSpec.IntValue strengthUpgradeMaxLevel;
    public final ModConfigSpec.IntValue strengthUpgradeBaseCost;
    public final ModConfigSpec.IntValue strengthUpgradeRampingCost;
    public final ModConfigSpec.DoubleValue strengthUpgradeDmgPerLevel;

    public final ModConfigSpec.IntValue resistanceUpgradeMaxLevel;
    public final ModConfigSpec.IntValue resistanceUpgradeBaseCost;
    public final ModConfigSpec.IntValue resistanceUpgradeRampingCost;
    public final ModConfigSpec.DoubleValue resistanceUpgradeReductionPerLevel;

    static {
        Pair<ModConfigValues, ModConfigSpec> pair =
                new ModConfigSpec.Builder().configure(ModConfigValues::new);
        CONFIG = pair.getLeft();
        CONFIG_SPEC = pair.getRight();
    }

    private ModConfigValues(ModConfigSpec.Builder builder) {

        builder.comment("Player stat settings").push("stats");

        builder.comment("Experience").push("general");

        baseLevelExp = builder
                .comment("Base exp per level (Level 0) [1 - 10000]")
                .defineInRange("base_level_exp", 30, 1, 10000);
        levelExpRamping = builder
                .comment("Exp increase needed for each level [1 - 10000]")
                .defineInRange("level_exp_ramping", 15, 1, 10000);
        pointsPerLevel = builder
                .comment("Points gained per level up [1 - 10000]")
                .defineInRange("points_per_level", 2, 1, 10000);
        maxLevel = builder
                .comment("Max level achievable, leave at 0 for auto [0 - 10000]")
                .defineInRange("max_level", 0, 0, 10000);

        foodExpMultiplier = builder
                .comment("Mutliplies food exp gained from food [0.1 - 500]")
                .defineInRange("food_exp_multiplier", 1.5, 0.1, 500);
        foodExpReductionPerUsage = builder
                .comment("Flat value that is subtracted from the exp given by food depending on how much was eaten [0 - 1000]")
                .defineInRange("food_exp_reduction_per_usage", 2, 0.0, 1000.0);

        builder.pop();

        builder.comment("Health").push("health");

        upgradeCooldown = builder
                .comment("Number of ticks between upgrading/deupgrading [0 - 72000]")
                .defineInRange("food_exp_multiplier", 600, 0, 72000);

        healthUpgradeMaxLevel = builder
                .comment("Max level for health upgrade [0 - 100]")
                .defineInRange("health_upgrade_max_level", 20, 0, 100);
        healthUpgradeBaseCost = builder
                .comment("Base cost for health upgrade [1 - 100]")
                .defineInRange("health_upgrade_base_cost", 2, 1, 100);
        healthUpgradeRampingCost = builder
                .comment("Cost added per level of health upgrade [1 - 100]")
                .defineInRange("health_upgrade_ramping_cost", 1, 1, 100);
        healthUpgradeHealthPerLevel = builder
                .comment("Health added per level of health upgrade [1 - 100]")
                .defineInRange("health_upgrade_health_per_level", 2, 1, 100);

        builder.pop();

        builder.comment("Regen").push("regen");

        regenUpgradeMaxLevel = builder
                .comment("Max level for regen upgrade [0 - 100]")
                .defineInRange("regen_upgrade_max_level", 10, 0, 100);
        regenUpgradeBaseCost = builder
                .comment("Base cost for regen upgrade [1 - 100]")
                .defineInRange("regen_upgrade_base_cost", 4, 1, 100);
        regenUpgradeRampingCost = builder
                .comment("Cost added per level of regen upgrade [1 - 100]")
                .defineInRange("regen_upgrade_ramping_cost", 2, 1, 100);
        regenUpgradeBaseInterval = builder
                .comment("Base interval in ticks at which a base level of regen works [1 - 1000]")
                .defineInRange("regen_upgrade_base_interval", 40, 1, 1000);
        regenUpgradeIntervalDecrease = builder
                .comment("The interval decrease per level [1 - 100]")
                .defineInRange("regen_upgrade_interval_decrease", 3, 1, 100);

        builder.pop();

        builder.comment("Haste").push("haste");

        hasteUpgradeMaxLevel = builder
                .comment("Max level for haste upgrade [0 - 100]")
                .defineInRange("haste_upgrade_max_level", 10, 0, 100);
        hasteUpgradeBaseCost = builder
                .comment("Base cost for haste upgrade [1 - 100]")
                .defineInRange("haste_upgrade_base_cost", 4, 1, 100);
        hasteUpgradeRampingCost = builder
                .comment("Cost added per level of haste upgrade [1 - 100]")
                .defineInRange("haste_upgrade_ramping_cost", 2, 1, 100);
        hasteUpgradeSpeedPerLevel = builder
                .comment("Haste speed per level of haste upgrade [0.0 - 100.0]")
                .defineInRange("haste_upgrade_speed_per_level", 0.2, 0.0, 100);

        builder.pop();

        builder.comment("Speed").push("speed");

        speedUpgradeMaxLevel = builder
                .comment("Max level for speed upgrade [0 - 100]")
                .defineInRange("speed_upgrade_max_level", 10, 0, 100);
        speedUpgradeBaseCost = builder
                .comment("Base cost for speed upgrade [1 - 100]")
                .defineInRange("speed_upgrade_base_cost", 4, 1, 100);
        speedUpgradeRampingCost = builder
                .comment("Cost added per level of speed upgrade [1 - 100]")
                .defineInRange("speed_upgrade_ramping_cost", 2, 1, 100);
        speedUpgradeSpeedPerLevel = builder
                .comment("Speed per level of haste upgrade [0.0 - 100.0]")
                .defineInRange("speed_upgrade_speed_per_level", 0.15, 0.0, 100);

        builder.pop();

        builder.comment("Strength").push("strength");

        strengthUpgradeMaxLevel = builder
                .comment("Max level for strength upgrade [0 - 100]")
                .defineInRange("strength_upgrade_max_level", 10, 0, 100);
        strengthUpgradeBaseCost = builder
                .comment("Base cost for strength upgrade [1 - 100]")
                .defineInRange("strength_upgrade_base_cost", 4, 1, 100);
        strengthUpgradeRampingCost = builder
                .comment("Cost added per level of strength upgrade [1 - 100]")
                .defineInRange("strength_upgrade_ramping_cost", 2, 1, 100);
        strengthUpgradeDmgPerLevel = builder
                .comment("Damage increase per level of strength [0.0 - 100.0]")
                .defineInRange("strength_upgrade_dmg_per_level", 0.15, 0.0, 100);

        builder.pop();

        builder.comment("Resistance").push("resistance");

        resistanceUpgradeMaxLevel = builder
                .comment("Max level for resistance upgrade [0 - 100]")
                .defineInRange("resistance_upgrade_max_level", 10, 0, 100);
        resistanceUpgradeBaseCost = builder
                .comment("Base cost for resistance upgrade [1 - 100]")
                .defineInRange("resistance_upgrade_base_cost", 4, 1, 100);
        resistanceUpgradeRampingCost = builder
                .comment("Cost added per level of resistance upgrade [1 - 100]")
                .defineInRange("resistance_upgrade_ramping_cost", 2, 1, 100);
        resistanceUpgradeReductionPerLevel = builder
                .comment("Damage increase per level of resistance [0.0 - 1.0]")
                .defineInRange("resistance_upgrade_reduction_per_level", 0.05, 0.0, 1.0);

        builder.pop();

        builder.pop();
    }
}
