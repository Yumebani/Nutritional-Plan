package net.yumebani.nutritionalplan.data;

import net.minecraft.util.Mth;
import net.yumebani.nutritionalplan.ModConfigValues;

public class NutritionUtils {

    public static int getFoodExp(float nutrition, float saturation, float timesEaten){
        double exp = ((nutrition + saturation) * ModConfigValues.CONFIG.foodExpMultiplier.get()) - (timesEaten * ModConfigValues.CONFIG.foodExpReductionPerUsage.get());
        if (exp <= 0) return 0;
        return Mth.ceil(exp);
    }

    public static int getExpForLevel(int level){
        return ModConfigValues.CONFIG.baseLevelExp.get() + (level * ModConfigValues.CONFIG.levelExpRamping.get());
    }

    public static int getPointCost(NutritionUpgrades upgrade, Integer level) {
        return upgrade.getBaseCost() + (upgrade.getRamping() * (level));
    }

    public static int getMaxLevel(){
        int totalPoints = 0;
        for (NutritionUpgrades upgrade : NutritionUpgrades.values()){
            for (int level = 0; level < upgrade.getMaxLevel(); level++){
                totalPoints += getPointCost(upgrade, level);
            }
        }
        return Mth.ceil((float)(totalPoints) / ModConfigValues.CONFIG.pointsPerLevel.get());
    }
}
