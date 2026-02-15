package org.teneted.neotenet.mixin.world.entity.projectile.windcharge;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.windcharge.AbstractWindCharge;
import net.minecraft.world.level.Level;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractWindCharge.class)
public abstract class MixinAbstractWindCharge extends AbstractHurtingProjectile {

    protected MixinAbstractWindCharge(EntityType<? extends AbstractHurtingProjectile> p_36833_, Level p_36834_) {
        super(p_36833_, p_36834_);
    }

    @Inject(method = {"onHitBlock", "onHit"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/windcharge/AbstractWindCharge;discard()V"))
    private void neotenet$pushDiscardReason(CallbackInfo ci) {
        this.pushRemoveCause(EntityRemoveEvent.Cause.HIT);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/windcharge/AbstractWindCharge;discard()V"))
    private void neotenet$discardReason(CallbackInfo ci) {
        this.pushRemoveCause(EntityRemoveEvent.Cause.HIT);
    }
}
