package org.teneted.neotenet.mixin.world.level.material;

import com.llamalad7.mixinextras.sugar.Cancellable;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.LavaFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LavaFluid.class)
public class MixinLavaFluid {

    @Inject(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z", ordinal = 0), cancellable = true)
    private void neotenet$callBlockIgniteEvent0(Level p_230572_, BlockPos p_230573_, FluidState p_230574_, RandomSource p_230575_, CallbackInfo ci, @Local(ordinal = 1) BlockPos blockpos) {
        // CraftBukkit start - Prevent lava putting something on fire
        if (p_230572_.getBlockState(blockpos).getBlock() != Blocks.FIRE) {
            if (!org.bukkit.craftbukkit.event.CraftEventFactory.callBlockIgniteEvent(p_230572_, blockpos, p_230573_).isCancelled()) {
                ci.cancel();
            }
        }
        // CraftBukkit end
    }

    @Inject(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z", ordinal = 1), cancellable = true)
    private void neotenet$callBlockIgniteEvent1(Level p_230572_, BlockPos p_230573_, FluidState p_230574_, RandomSource p_230575_, CallbackInfo ci, @Local(ordinal = 1) BlockPos blockpos) {
        // CraftBukkit start - Prevent lava putting something on fire
        BlockPos up = blockpos.above();
        if (p_230572_.getBlockState(up).getBlock() != Blocks.FIRE) {
            if (!org.bukkit.craftbukkit.event.CraftEventFactory.callBlockIgniteEvent(p_230572_, up, blockpos).isCancelled()) {
                ci.cancel();
            }
        }
        // CraftBukkit end
    }

    @Redirect(method = "spreadTo", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/LevelAccessor;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private boolean neotenet$handleBlockFormEvent(LevelAccessor instance, BlockPos blockPos, BlockState blockState, int i, @Cancellable CallbackInfo ci) {
        // CraftBukkit start
        if (!org.bukkit.craftbukkit.event.CraftEventFactory.handleBlockFormEvent(instance.getMinecraftWorld(), blockPos, Blocks.STONE.defaultBlockState(), 3)) {
            ci.cancel();
        }
        // CraftBukkit end
        return true;
    }
}
