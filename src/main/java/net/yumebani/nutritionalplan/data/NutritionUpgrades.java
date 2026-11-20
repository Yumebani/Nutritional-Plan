package net.yumebani.nutritionalplan.data;

import net.yumebani.nutritionalplan.ModConfigValues;
import net.yumebani.nutritionalplan.screen.NutritionConfigScreen;

import java.util.function.Supplier;

public enum NutritionUpgrades {
    HEALTH(ModConfigValues.CONFIG.healthUpgradeMaxLevel,
            ModConfigValues.CONFIG.healthUpgradeBaseCost,
            ModConfigValues.CONFIG.healthUpgradeRampingCost),
    REGENERATION(ModConfigValues.CONFIG.regenUpgradeMaxLevel,
            ModConfigValues.CONFIG.regenUpgradeBaseCost,
            ModConfigValues.CONFIG.regenUpgradeRampingCost),
    SPEED(ModConfigValues.CONFIG.speedUpgradeMaxLevel,
            ModConfigValues.CONFIG.speedUpgradeBaseCost,
            ModConfigValues.CONFIG.speedUpgradeRampingCost),
    RESISTANCE(ModConfigValues.CONFIG.resistanceUpgradeMaxLevel,
            ModConfigValues.CONFIG.resistanceUpgradeBaseCost,
            ModConfigValues.CONFIG.resistanceUpgradeRampingCost),
    HASTE(ModConfigValues.CONFIG.hasteUpgradeMaxLevel,
            ModConfigValues.CONFIG.hasteUpgradeBaseCost,
            ModConfigValues.CONFIG.hasteUpgradeRampingCost),
    STRENGTH(ModConfigValues.CONFIG.strengthUpgradeMaxLevel,
            ModConfigValues.CONFIG.strengthUpgradeBaseCost,
            ModConfigValues.CONFIG.strengthUpgradeRampingCost);

    Supplier<Integer> maxLevel;
    Supplier<Integer> baseCost;
    Supplier<Integer> rampingCost;

    NutritionUpgrades(Supplier<Integer> maxLevel, Supplier<Integer> baseCost, Supplier<Integer> rampingCost){
        this.maxLevel = maxLevel;
        this.baseCost = baseCost;
        this.rampingCost = rampingCost;
    }

    public int getMaxLevel() {
        return maxLevel.get();
    }

    public int getBaseCost() {
        return baseCost.get();
    }

    public int getRamping() {
        return rampingCost.get();
    }
}
