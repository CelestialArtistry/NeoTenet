package org.teneted.neotenet.mixin.world.entity.projectile;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FireworkRocketEntity.class)
public abstract class MixinFireworkRocketEntity extends Projectile {

    protected MixinFireworkRocketEntity(EntityType<? extends Projectile> p_37248_, Level p_37249_) {
        super(p_37248_, p_37249_);
    }

    @WrapWithCondition(method = {"tick", "onHitEntity", "onHitBlock"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/FireworkRocketEntity;explode()V"))
    private boolean neotenet$callFireworkExplodeEvent(FireworkRocketEntity instance) {
        return !CraftEventFactory.callFireworkExplodeEvent(((FireworkRocketEntity) (Object) this)).isCancelled();
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/FireworkRocketEntity;hitTargetOrDeflectSelf(Lnet/minecraft/world/phys/HitResult;)Lnet/minecraft/world/entity/projectile/ProjectileDeflection;"))
    private ProjectileDeflection neotenet$resetHit(FireworkRocketEntity instance, HitResult hitResult) {
        return preHitTargetOrDeflectSelf(hitResult);
    }

    @Inject(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/FireworkRocketEntity;discard()V"))
    private void neotenet$discardReason(CallbackInfo ci) {
        this.pushRemoveCause(EntityRemoveEvent.Cause.EXPLODE);
    }
}
