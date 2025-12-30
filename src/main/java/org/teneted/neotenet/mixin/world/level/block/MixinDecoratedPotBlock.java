package org.teneted.neotenet.mixin.world.level.block;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DecoratedPotBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DecoratedPotBlock.class)
public abstract class MixinDecoratedPotBlock {

    @Shadow
    protected abstract FluidState getFluidState(BlockState p_272593_);

    @Inject(method = "onProjectileHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private void neotenet$callEntityChangeBlockEvent(Level p_306322_, BlockState p_306005_, BlockHitResult p_306105_, Projectile p_305851_, CallbackInfo ci, @Local(ordinal = 0) BlockPos blockpos) {
        // CraftBukkit start - call EntityChangeBlockEvent
        if (!org.bukkit.craftbukkit.event.CraftEventFactory.callEntityChangeBlockEvent(p_305851_, blockpos, this.getFluidState(p_306005_).createLegacyBlock())) {
            ci.cancel();
            return;
        }
        // CraftBukkit end
    }
}
