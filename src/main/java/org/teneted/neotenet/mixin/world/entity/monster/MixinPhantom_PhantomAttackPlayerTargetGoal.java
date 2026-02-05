package org.teneted.neotenet.mixin.world.entity.monster;

import net.minecraft.world.entity.monster.Phantom;
import org.bukkit.event.entity.EntityTargetEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.entity.monster.Phantom.PhantomAttackPlayerTargetGoal")
public class MixinPhantom_PhantomAttackPlayerTargetGoal {

    @Shadow
    @Final
    private Phantom this$0;

    @Inject(method = "canUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/Phantom;setTarget(Lnet/minecraft/world/entity/LivingEntity;)V"))
    private void neotenet$pushTargetReason(CallbackInfoReturnable<Boolean> cir) {
        this$0.bridge$pushGoalTargetReason(EntityTargetEvent.TargetReason.CLOSEST_PLAYER, true);
    }
}
