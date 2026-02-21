package org.teneted.neotenet.mixin.world.entity.projectile;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Direction;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.teneted.neotenet.injection.world.entity.projectile.InjectionShulkerBullet;

import javax.annotation.Nullable;

@Mixin(ShulkerBullet.class)
public abstract class MixinShulkerBullet extends Projectile implements InjectionShulkerBullet {

    @Shadow
    @Nullable
    private Entity finalTarget;

    @Shadow
    @Nullable
    private Direction currentMoveDirection;

    @Shadow
    protected abstract void selectNextMoveDirection(@org.jetbrains.annotations.Nullable Direction.Axis p_37349_);

    @Shadow
    protected abstract void destroy();

    protected MixinShulkerBullet(EntityType<? extends Projectile> p_37248_, Level p_37249_) {
        super(p_37248_, p_37249_);
    }

    @Inject(method = "<init>(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/Direction$Axis;)V", at = @At("RETURN"))
    private void neotenet$setProjectileSource(Level p_37330_, LivingEntity p_37331_, Entity p_37332_, Direction.Axis p_37333_, CallbackInfo ci) {
        projectileSource = (org.bukkit.entity.LivingEntity) p_37331_.getBukkitEntity(); // CraftBukkit
    }

    // CraftBukkit start
    @Override
    public Entity getTarget() {
        return this.finalTarget;
    }

    @Override
    public void setTarget(Entity e) {
        this.finalTarget = e;
        this.currentMoveDirection = Direction.UP;
        this.selectNextMoveDirection(Direction.Axis.X);
    }
    // CraftBukkit end

    @Inject(method = "checkDespawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/ShulkerBullet;discard()V"))
    private void neotenet$discardReason(CallbackInfo ci) {
        this.pushRemoveCause(EntityRemoveEvent.Cause.DESPAWN);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/ShulkerBullet;hitTargetOrDeflectSelf(Lnet/minecraft/world/phys/HitResult;)Lnet/minecraft/world/entity/projectile/ProjectileDeflection;"))
    private ProjectileDeflection neotenet$usePreHitTargetOrDeflectSelf(ShulkerBullet instance, HitResult hitResult) {
        return this.preHitTargetOrDeflectSelf(hitResult);
    }

    @Inject(method = "onHitEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z"))
    private void neotenet$pushEffectCause(EntityHitResult p_37345_, CallbackInfo ci, @Local(ordinal = 1) LivingEntity livingentity1) {
        livingentity1.pushEffectCause(EntityPotionEffectEvent.Cause.ATTACK);
    }

    @Inject(method = "destroy", at = @At("HEAD"))
    private void neotenet$destroyCause(CallbackInfo ci) {
        this.pushRemoveCause(null);
    }

    private void destroy(EntityRemoveEvent.Cause cause) {
        pushRemoveCause(cause);
        destroy();
    }

    @Inject(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/ShulkerBullet;destroy()V"))
    private void neotenet$pushDestroyReason(HitResult p_37347_, CallbackInfo ci) {
        this.pushRemoveCause(EntityRemoveEvent.Cause.HIT);
    }

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void neotenet$handleNonLivingEntityDamageEvent(DamageSource p_37338_, float p_37339_, CallbackInfoReturnable<Boolean> cir) {
        // CraftBukkit start
        if (org.bukkit.craftbukkit.event.CraftEventFactory.handleNonLivingEntityDamageEvent(((ShulkerBullet) (Object) this), p_37338_, p_37339_, false)) {
            cir.setReturnValue(false);
        }
        this.pushRemoveCause(EntityRemoveEvent.Cause.DEATH);
        // CraftBukkit end
    }
}
