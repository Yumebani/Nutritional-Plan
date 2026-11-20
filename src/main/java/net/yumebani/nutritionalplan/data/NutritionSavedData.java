package net.yumebani.nutritionalplan.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.neoforge.network.PacketDistributor;
import net.yumebani.nutritionalplan.ModConfigValues;
import net.yumebani.nutritionalplan.attributes.ModAttributes;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class NutritionSavedData extends SavedData {

    private Map<UUID, Map<String, Integer>> eatenFoodsPlayers = new HashMap<>();
    private Map<UUID, Map<NutritionUpgrades, Integer>> nutritionUpgrades = new HashMap<>();
    private Map<UUID, Integer> expPlayers = new HashMap<>();
    private Map<UUID, Integer> level = new HashMap<>();
    private Map<UUID, Integer> points = new HashMap<>();
    private Map<UUID, Integer> cooldown = new HashMap<>();

    public boolean hasNewPlayer(UUID player){
        return !eatenFoodsPlayers.containsKey(player);
    }

    public void registerNewPlayer(UUID player){
        eatenFoodsPlayers.put(player, new HashMap<>());
        nutritionUpgrades.put(player, new HashMap<>());
        for (NutritionUpgrades upgrade : NutritionUpgrades.values()){
            nutritionUpgrades.get(player).put(upgrade, 0);
        }
        expPlayers.put(player, 0);
        level.put(player, 0);
        points.put(player, 0);
        cooldown.put(player, 0);
    }

    public void tick(){
        for (UUID id : cooldown.keySet().stream().toList()){
            int currentCooldown = cooldown.get(id);
            if (currentCooldown > 0){
                cooldown.put(id, currentCooldown - 1);
            }
        }
    }

    public void resetCooldown(UUID player){
        cooldown.put(player, 0);
    }

    public ClientBoundFoodDataPacket createPacket(UUID player){
        return new ClientBoundFoodDataPacket(eatenFoodsPlayers.get(player), nutritionUpgrades.get(player), expPlayers.get(player), level.get(player), points.get(player), cooldown.get(player));
    }

    public void foodEaten(UUID player, Item foodItem){

        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(foodItem);

        if (!itemId.equals(BuiltInRegistries.ITEM.getDefaultKey())){

            Map<String, Integer> eatenFoods = eatenFoodsPlayers.get(player);

            addExp(player, getFoodExp(player, foodItem));

            if (!eatenFoods.containsKey(itemId.toString())){
                eatenFoods.put(itemId.toString(), 1);
            } else {
                eatenFoods.put(itemId.toString(), eatenFoods.get(itemId.toString()) + 1);
            }

            setDirty();
        }
    }

    public int getFoodExp(UUID player, Item foodItem){
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(foodItem);

        if (!itemId.equals(BuiltInRegistries.ITEM.getDefaultKey())){

            FoodProperties foodProperties = foodItem.components().get(DataComponents.FOOD);
            if (eatenFoodsPlayers.get(player).containsKey(itemId.toString())){
                return NutritionUtils.getFoodExp(foodProperties.nutrition(), foodProperties.saturation(), eatenFoodsPlayers.get(player).get(itemId.toString()));
            }
            return NutritionUtils.getFoodExp(foodProperties.nutrition(), foodProperties.saturation(), 0);

        }

        return -1;
    }

    public void addExp(UUID player, int count){
        this.expPlayers.put(player, this.expPlayers.get(player) + count);
        while (this.expPlayers.get(player) >= NutritionUtils.getExpForLevel(this.level.get(player)) && this.level.get(player) < NutritionUtils.getMaxLevel()){
            this.expPlayers.put(player, this.expPlayers.get(player) - NutritionUtils.getExpForLevel(this.level.get(player)));
            levelUp(player, 1);
        }
        if (this.level.get(player) == NutritionUtils.getMaxLevel()){
            this.expPlayers.put(player, 0);
        }
        setDirty();
    }

    public void subExp(UUID player, int count){
        this.expPlayers.put(player, this.expPlayers.get(player) - count);
        while (this.expPlayers.get(player) < 0 && this.level.get(player) > 0){
            this.expPlayers.put(player, this.expPlayers.get(player) + NutritionUtils.getExpForLevel(this.level.get(player) - 1));
            delevelUp(player, 1);
        }
        if (this.level.get(player) == 0){
            this.expPlayers.put(player, 0);
        }
        setDirty();
    }

    public void levelUp(UUID player, int count){
        for (int i = 0; i < count; i++){
            if (this.level.get(player) < NutritionUtils.getMaxLevel()){
                this.level.put(player, this.level.get(player) + 1);
                addPoints(player, ModConfigValues.CONFIG.pointsPerLevel.get());
            } else break;
        }
        setDirty();
    }

    public void addPoints(UUID player, int count){
        this.points.put(player, this.points.get(player) + count);
        setDirty();
    }

    public void delevelUp(UUID player, int count){
        for (int i = 0; i < count; i++){
            if (this.level.get(player) > 0){
                this.level.put(player, this.level.get(player) - 1);
                subPoints(player, ModConfigValues.CONFIG.pointsPerLevel.get());
            } else break;
        }
        setDirty();
    }

    public void subPoints(UUID player, int count){
        this.points.put(player, this.points.get(player) - count);
        if (this.points.get(player) < 0){
            this.points.put(player, 0);
        }
        setDirty();
    }

    public int getNutritionUpgrade(UUID player, NutritionUpgrades upgrade){
        return this.nutritionUpgrades.get(player).get(upgrade);
    }

    public int getPointCost(UUID player, NutritionUpgrades upgrade){
        return NutritionUtils.getPointCost(upgrade, this.nutritionUpgrades.get(player).get(upgrade));
    }

    public void spendPoints(MinecraftServer server, UUID player, NutritionUpgrades upgrade){
        this.points.put(player, this.points.get(player) - getPointCost(player, upgrade));
        this.nutritionUpgrades.get(player).put(upgrade, this.nutritionUpgrades.get(player).get(upgrade) + 1);
        this.cooldown.put(player, ModConfigValues.CONFIG.upgradeCooldown.get());
        applyNewModifiers(server, player);
        setDirty();
    }

    public int getPointCostPrev(UUID player, NutritionUpgrades upgrade){
        return NutritionUtils.getPointCost(upgrade, this.nutritionUpgrades.get(player).get(upgrade) - 1);
    }

    public void unspendPoints(MinecraftServer server, UUID player, NutritionUpgrades upgrade){
        this.points.put(player, this.points.get(player) + getPointCostPrev(player, upgrade));
        this.nutritionUpgrades.get(player).put(upgrade, this.nutritionUpgrades.get(player).get(upgrade) - 1);
        this.cooldown.put(player, ModConfigValues.CONFIG.upgradeCooldown.get());
        applyNewModifiers(server, player);
        setDirty();
    }

    public void applyNewModifiers(MinecraftServer server, UUID player){
        ModAttributes.addMaxHealth(server.getPlayerList().getPlayer(player), this.getNutritionUpgrade(player, NutritionUpgrades.HEALTH) * ModConfigValues.CONFIG.healthUpgradeHealthPerLevel.get());
        ModAttributes.setBlockBreakSpeedBonus(server.getPlayerList().getPlayer(player), this.getNutritionUpgrade(player, NutritionUpgrades.HASTE) * ModConfigValues.CONFIG.hasteUpgradeSpeedPerLevel.get());
        ModAttributes.setRunSpeedBonus(server.getPlayerList().getPlayer(player), this.getNutritionUpgrade(player, NutritionUpgrades.SPEED) * ModConfigValues.CONFIG.speedUpgradeSpeedPerLevel.get());
    }

    public static NutritionSavedData create(){
        return new NutritionSavedData();
    }

    public NutritionSavedData loadData(Map<UUID, Map<String, Integer>> eatenFoodsPlayers,
                                       Map<UUID, Map<NutritionUpgrades, Integer>> nutritionUpgrades,
                                       Map<UUID, Integer> expPlayers,
                                       Map<UUID, Integer> level,
                                       Map<UUID, Integer> points,
                                       Map<UUID, Integer> cooldown){

        this.eatenFoodsPlayers = eatenFoodsPlayers;
        this.nutritionUpgrades = nutritionUpgrades;
        this.expPlayers = expPlayers;
        this.level = level;
        this.points = points;
        this.cooldown = cooldown;
        return this;
    }

    public static NutritionSavedData load(CompoundTag tag, HolderLookup.Provider levelRegistry){
        NutritionSavedData data = NutritionSavedData.create();
        Map<UUID, Map<String, Integer>> eatenFoodsPlayers = new HashMap<>();
        Map<UUID, Map<NutritionUpgrades, Integer>> nutritionUpgrades = new HashMap<>();
        Map<UUID, Integer> expPlayers = new HashMap<>();
        Map<UUID, Integer> level = new HashMap<>();
        Map<UUID, Integer> points = new HashMap<>();
        Map<UUID, Integer> cooldown = new HashMap<>();
        int uuidCount = tag.getInt("uuid_count");
        for (int i = 0; i < uuidCount; i++){
            UUID uuid = tag.getUUID("uuid_" + i);
            eatenFoodsPlayers.put(uuid, new HashMap<>());
            int foodCount = tag.getInt("uuid_" + i + "_eaten_count");
            for (int ii = 0; ii < foodCount; ii++){
                eatenFoodsPlayers.get(uuid).put(
                        tag.getString("uuid_" + i + "_eaten_food_" + ii),
                        tag.getInt("uuid_" + i + "_eaten_food_" + ii + "_count"));
            }
            nutritionUpgrades.put(uuid, new HashMap<>());
            for (int ii = 0; ii < NutritionUpgrades.values().length; ii++){
                nutritionUpgrades.get(uuid).put(
                        NutritionUpgrades.values()[ii],
                        tag.getInt("uuid_" + i + "_upgrade_" + ii));
            }
            expPlayers.put(uuid, tag.getInt("uuid_" + i + "_exp"));
            level.put(uuid, tag.getInt("uuid_" + i + "_level"));
            points.put(uuid, tag.getInt("uuid_" + i + "_points"));
            cooldown.put(uuid, tag.getInt("uuid_" + i + "_cooldown"));
        }
        return data.loadData(eatenFoodsPlayers, nutritionUpgrades, expPlayers, level, points, cooldown);
    }

    public static final SavedData.Factory<NutritionSavedData> FACTORY = new SavedData.Factory<>(
            NutritionSavedData::create,
            NutritionSavedData::load,
            DataFixTypes.SAVED_DATA_MAP_DATA
    );

    public static NutritionSavedData computeIfAbsent(MinecraftServer server){
        return server.overworld().getDataStorage().computeIfAbsent(FACTORY, "nutrition_data");
    }

    @Override
    public CompoundTag save(CompoundTag pCompoundTag, HolderLookup.Provider provider) {
        pCompoundTag.putInt("uuid_count", eatenFoodsPlayers.size());
        for (int i = 0; i < eatenFoodsPlayers.size(); i++){
            UUID uuid = eatenFoodsPlayers.keySet().stream().toList().get(i);
            pCompoundTag.putUUID("uuid_" + i, uuid);
            pCompoundTag.putInt("uuid_" + i + "_eaten_count", eatenFoodsPlayers.get(uuid).size());
            for (int ii = 0; ii < eatenFoodsPlayers.get(uuid).size(); ii++){
                pCompoundTag.putString("uuid_" + i + "_eaten_food_" + ii, eatenFoodsPlayers.get(uuid).keySet().stream().toList().get(ii));
                pCompoundTag.putInt("uuid_" + i + "_eaten_food_" + ii + "_count", eatenFoodsPlayers.get(uuid).values().stream().toList().get(ii));
            }
            for (int ii = 0; ii < NutritionUpgrades.values().length; ii++){
                pCompoundTag.putInt("uuid_" + i + "_upgrade_" + ii, nutritionUpgrades.get(uuid).get(NutritionUpgrades.values()[ii]));
            }
            pCompoundTag.putInt("uuid_" + i + "_exp", this.expPlayers.get(uuid));
            pCompoundTag.putInt("uuid_" + i + "_level", this.level.get(uuid));
            pCompoundTag.putInt("uuid_" + i + "_points", this.points.get(uuid));
            pCompoundTag.putInt("uuid_" + i + "_cooldown", this.cooldown.get(uuid));
        }
        return pCompoundTag;

    }

    public boolean tryReset(ServerPlayer serverPlayer) {
        if (this.eatenFoodsPlayers.containsKey(serverPlayer.getUUID())){
            clearPlayerData(serverPlayer);
            setDirty();
            return true;
        }
        return false;
    }

    public void clearPlayerData(ServerPlayer serverPlayer){
        registerNewPlayer(serverPlayer.getUUID());
    }

    public void updatePlayerData(ServerPlayer serverPlayer){
        PacketDistributor.sendToPlayer(serverPlayer, createPacket(serverPlayer.getUUID()));
    }
}
