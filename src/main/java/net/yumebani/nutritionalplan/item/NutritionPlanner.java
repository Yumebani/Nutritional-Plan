package net.yumebani.nutritionalplan.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.yumebani.nutritionalplan.screen.NutritionConfigScreen;

public class NutritionPlanner extends Item {
    public NutritionPlanner(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);

        if (level.isClientSide()) {
            NutritionConfigScreen.open();
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
