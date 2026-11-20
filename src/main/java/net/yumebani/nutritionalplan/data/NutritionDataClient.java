package net.yumebani.nutritionalplan.data;

import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.network.PacketDistributor;
import net.yumebani.nutritionalplan.ModConfigValues;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class NutritionDataClient {

    private Map<String, Integer> eatenFoods;
    private Map<NutritionUpgrades, Integer> nutritionUpgrades;
    private int exp;
    private int level;
    private int points;
    private int cooldown;

    private static NutritionDataClient INSTANCE;

    public static NutritionDataClient getInstance(){
        if (INSTANCE == null){
            INSTANCE = new NutritionDataClient();
        }

        return INSTANCE;
    }

    private NutritionDataClient(){
        this.eatenFoods = new HashMap<>();
        this.nutritionUpgrades = new HashMap<>();
        for (NutritionUpgrades upgrade : NutritionUpgrades.values()){
            this.nutritionUpgrades.put(upgrade, 0);
        }
        this.exp = 0;
        this.level = 0;
        this.points = 0;
        this.cooldown = 0;
    }

    public void loadClientBoundData(Map<String, Integer> eatenFoods, Map<NutritionUpgrades, Integer> nutritionUpgrades, int exp, int level, int points, int cooldown){
        this.eatenFoods = eatenFoods;
        this.nutritionUpgrades = nutritionUpgrades;
        this.exp = exp;
        this.level = level;
        this.points = points;
        this.cooldown = cooldown;
    }

    public void tick(){
        if (isOnCooldown()){
            cooldown--;
        }
    }

    public boolean isOnCooldown(){
        return cooldown > 0;
    }

    public void foodEaten(Item foodItem){
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(foodItem);

        if (!itemId.equals(BuiltInRegistries.ITEM.getDefaultKey())){

            int exp = getFoodExp(foodItem);
            if (exp > 0){
                addExp(getFoodExp(foodItem));
            }

            if (!eatenFoods.containsKey(itemId.toString())){
                eatenFoods.put(itemId.toString(), 1);
            } else {
                eatenFoods.put(itemId.toString(), eatenFoods.get(itemId.toString()) + 1);
            }
        }
    }

    public int getFoodEaten(Item foodItem){
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(foodItem);

        if (!itemId.equals(BuiltInRegistries.ITEM.getDefaultKey())){
            return eatenFoods.getOrDefault(itemId.toString(), 0);
        }

        return -1;
    }

    public int getFoodExp(Item foodItem){
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(foodItem);

        if (!itemId.equals(BuiltInRegistries.ITEM.getDefaultKey())){

            FoodProperties foodProperties = foodItem.components().get(DataComponents.FOOD);
            if (foodProperties != null){
                if (eatenFoods.containsKey(itemId.toString())){
                    return NutritionUtils.getFoodExp(foodProperties.nutrition(), foodProperties.saturation(), eatenFoods.get(itemId.toString()));
                }
                return NutritionUtils.getFoodExp(foodProperties.nutrition(), foodProperties.saturation(), 0);
            }
        }

        return -1;
    }

    public void addExp(int count){
        boolean leveledUp = false;
        this.exp += count;
        while (this.exp >= NutritionUtils.getExpForLevel(this.level) && this.level < NutritionUtils.getMaxLevel()){
            this.exp -= NutritionUtils.getExpForLevel(this.level);
            levelUp(1);
            leveledUp = true;
        }
        if (this.level == NutritionUtils.getMaxLevel()){
            this.exp = 0;
        }

        if (leveledUp){
            Player player = Minecraft.getInstance().player;
            player.level().playSound(player, player.blockPosition(), SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS);
            player.displayClientMessage(Component.literal("§6Leveled up!"), true);
        } else {
            Player player = Minecraft.getInstance().player;
            player.displayClientMessage(Component.literal("§6Gained " + count + " nutri-xp"), false);
        }
    }

    public void subExp(int count){
        this.exp -= count;
        while (this.exp < 0 && this.level > 0){
            this.exp += NutritionUtils.getExpForLevel(this.level - 1);
            delevelUp(1);
        }
        if (this.level == 0){
            this.exp = 0;
        }
    }

    public void levelUp(int count){
        for (int i = 0; i < count; i++){
            if (this.level < NutritionUtils.getMaxLevel()){
                this.level++;
                addPoints(ModConfigValues.CONFIG.pointsPerLevel.get());
            } else break;
        }
    }

    public void addPoints(int count){
        this.points += count;
    }

    public void delevelUp(int count){
        for (int i = 0; i < count; i++){
            if (this.level > 0){
                this.level--;
                subPoints(ModConfigValues.CONFIG.pointsPerLevel.get());
            } else break;
        }
    }

    public void subPoints(int count){
        this.points -= count;
        if (this.points < 0){
            this.points = 0;
        }
    }

    public int getPoints() {
        return points;
    }

    public int getExp() {
        return exp;
    }

    public int getLevel() {
        return level;
    }

    public int getNutritionUpgrade(NutritionUpgrades upgrade){
        return this.nutritionUpgrades.get(upgrade);
    }

    public int getPointCost(NutritionUpgrades upgrade){
        return NutritionUtils.getPointCost(upgrade, this.nutritionUpgrades.get(upgrade));
    }

    public boolean canUpgrade(NutritionUpgrades upgrade){
        return this.points >= getPointCost(upgrade) && getNutritionUpgrade(upgrade) < upgrade.getMaxLevel();
    }

    public void spendPoints(NutritionUpgrades upgrade){
        this.points -= getPointCost(upgrade);
        this.nutritionUpgrades.put(upgrade, this.nutritionUpgrades.get(upgrade) + 1);
        this.cooldown = ModConfigValues.CONFIG.upgradeCooldown.get();

        PacketDistributor.sendToServer(new ServerBoundFoodDataPacket(Optional.empty(), Optional.of(upgrade), true));
    }

    public boolean canUnupgrade(NutritionUpgrades upgrade){
        return getNutritionUpgrade(upgrade) > 0;
    }

    public int getPointCostPrev(NutritionUpgrades upgrade){
        return NutritionUtils.getPointCost(upgrade, this.nutritionUpgrades.get(upgrade) - 1);
    }

    public void unspendPoints(NutritionUpgrades upgrade){
        this.points += getPointCostPrev(upgrade);
        this.nutritionUpgrades.put(upgrade, this.nutritionUpgrades.get(upgrade) - 1);
        this.cooldown = ModConfigValues.CONFIG.upgradeCooldown.get();

        PacketDistributor.sendToServer(new ServerBoundFoodDataPacket(Optional.empty(), Optional.of(upgrade), false));
    }

    public boolean isMaxLevel(NutritionUpgrades upgrade) {
        return getNutritionUpgrade(upgrade) >= upgrade.getMaxLevel();
    }

    public String getCooldownFormated() {
        int localCooldown = this.cooldown;
        int minute = Mth.floor(localCooldown / 1200f);
        localCooldown %= 1200;
        int second = Mth.floor(localCooldown / 20f);
        StringBuilder builder = new StringBuilder();
        if (minute > 0){builder.append(minute + "m ");}
        if (second > 0){builder.append(second + "s"); return builder.toString();}
        else {return "Clear";}
    }
}
