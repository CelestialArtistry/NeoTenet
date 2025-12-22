package org.taiyitistmc.mixin.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.taiyitistmc.injection.world.level.block.InjectionBlock;

@Mixin(Block.class)
public abstract class MixinBlock implements InjectionBlock {


    @Override
    public int getExpDrop(BlockState blockState, ServerLevel world, BlockPos blockPos, ItemStack itemStack, boolean flag) {
        return 0;
    }


    @Override
    public int taiyitist$tryDropExperience(ServerLevel level, BlockPos pos, ItemStack heldItem, IntProvider amount) {
        int i = EnchantmentHelper.processBlockExperience(level, heldItem, amount.sample(level.getRandom()));
        return i;
    }
}
