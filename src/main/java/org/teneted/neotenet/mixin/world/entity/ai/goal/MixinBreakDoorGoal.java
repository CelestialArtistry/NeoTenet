package org.teneted.neotenet.mixin.world.entity.ai.goal;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.BreakDoorGoal;
import net.minecraft.world.entity.ai.goal.DoorInteractGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BreakDoorGoal.class)
public abstract class MixinBreakDoorGoal extends DoorInteractGoal {

    public MixinBreakDoorGoal(Mob p_25193_) {
        super(p_25193_);
    }

    @Shadow
    public abstract void start();

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;removeBlock(Lnet/minecraft/core/BlockPos;Z)Z"), cancellable = true)
    private void neotenet$callEntityBreakDoorEvent(CallbackInfo ci) {
        // CraftBukkit start
        if (org.bukkit.craftbukkit.event.CraftEventFactory.callEntityBreakDoorEvent(this.mob, this.doorPos).isCancelled()) {
            this.start();
            ci.cancel();
            return;
        }
        // CraftBukkit end
    }
}
