package org.teneted.neotenet.mixin.world.level.block;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MushroomBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.TreeType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MushroomBlock.class)
public class MixinMushroomBlock {

    @Redirect(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private boolean neotenet$handleBlockSpreadEvent(ServerLevel instance, BlockPos pos, BlockState blockState, int i, @Local(ordinal = 0, argsOnly = true) BlockPos p_221786_) {
        return org.bukkit.craftbukkit.event.CraftEventFactory.handleBlockSpreadEvent(instance, p_221786_, pos, blockState, 2); // CraftBukkit
    }

    @Inject(method = "growMushroom", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;removeBlock(Lnet/minecraft/core/BlockPos;Z)Z", shift = At.Shift.AFTER))
    private void neotenet$markTreeType(ServerLevel p_221774_, BlockPos p_221775_, BlockState p_221776_, RandomSource p_221777_, CallbackInfoReturnable<Boolean> cir) {
        SaplingBlock.treeType = (((MushroomBlock) (Object) this) == Blocks.BROWN_MUSHROOM) ? TreeType.BROWN_MUSHROOM : TreeType.RED_MUSHROOM; // CraftBukkit
    }
}
