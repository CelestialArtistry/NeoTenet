package org.teneted.neotenet.mixin.world.entity.ai.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.player.Player;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(TemptGoal.class)
public class MixinTemptGoal {

    @Shadow
    @Nullable
    protected Player player;

    @Shadow
    @Final
    protected PathfinderMob mob;

    @Inject(method = "canUse", at = @At("RETURN"), cancellable = true)
    private void neotenet$callEntityTargetLivingEvent(CallbackInfoReturnable<Boolean> cir) {
        // CraftBukkit start
        if (this.player != null && this.player instanceof LivingEntity livingEntity) {
            EntityTargetLivingEntityEvent event = CraftEventFactory.callEntityTargetLivingEvent(this.mob, livingEntity, EntityTargetEvent.TargetReason.TEMPT);
            if (event.isCancelled()) {
                cir.setReturnValue(false);
            }
            livingEntity = (event.getTarget() == null) ? null : ((CraftLivingEntity) event.getTarget()).getHandle();
        }
        // CraftBukkit end
    }
}
