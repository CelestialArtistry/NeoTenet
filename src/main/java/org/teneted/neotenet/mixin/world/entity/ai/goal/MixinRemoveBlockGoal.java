package org.teneted.neotenet.mixin.world.entity.ai.goal;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.RemoveBlockGoal;
import net.minecraft.world.level.Level;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RemoveBlockGoal.class)
public class MixinRemoveBlockGoal {

    @Shadow
    @Final
    private Mob removerMob;

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;removeBlock(Lnet/minecraft/core/BlockPos;Z)Z"), cancellable = true)
    private void neotenet$callEntityInteractEvent(CallbackInfo ci, @Local Level level, @Local(ordinal = 1) BlockPos blockpos1) {
        // CraftBukkit start - Step on eggs
        if (!CraftEventFactory.callEntityInteractEvent(this.removerMob, CraftBlock.at(level, blockpos1))) {
            ci.cancel();
            return;
        }
        // CraftBukkit end
    }
}
