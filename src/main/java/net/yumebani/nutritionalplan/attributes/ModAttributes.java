package net.yumebani.nutritionalplan.attributes;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.yumebani.nutritionalplan.NutritionalPlanMod;

public class ModAttributes {

    public static void setModifier(
            ServerPlayer player,
            Holder<Attribute> attribute,
            ResourceLocation modifierId,
            double newValue,
            AttributeModifier.Operation operation
    ) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance == null) {
            return;
        }

        if (newValue == 0.0) {
            instance.removeModifier(modifierId);
        } else {
            instance.addOrReplacePermanentModifier(new AttributeModifier(modifierId, newValue, operation));
        }
    }

    private static final ResourceLocation MAX_HEALTH_MOD =
            ResourceLocation.fromNamespaceAndPath(NutritionalPlanMod.MODID, "max_health");

    public static void addMaxHealth(ServerPlayer player, double bonusHealth) {
        setModifier(player, Attributes.MAX_HEALTH, MAX_HEALTH_MOD, bonusHealth, AttributeModifier.Operation.ADD_VALUE);
        if (player.getHealth() > player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
        }
    }

    private static final ResourceLocation RUN_SPEED_MOD =
            ResourceLocation.fromNamespaceAndPath(NutritionalPlanMod.MODID, "run_speed_mod");

    public static void setRunSpeedBonus(ServerPlayer player, double bonusPercent) {
        setModifier(
                player,
                Attributes.MOVEMENT_SPEED,
                RUN_SPEED_MOD,
                bonusPercent,
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE
        );
    }
    private static final ResourceLocation BREAK_SPEED_MOD =
            ResourceLocation.fromNamespaceAndPath(NutritionalPlanMod.MODID, "break_speed_mod");

    public static void setBlockBreakSpeedBonus(ServerPlayer player, double bonusAmount) {
        setModifier(
                player,
                Attributes.BLOCK_BREAK_SPEED,
                BREAK_SPEED_MOD,
                bonusAmount,
                AttributeModifier.Operation.ADD_VALUE
        );
    }
}
