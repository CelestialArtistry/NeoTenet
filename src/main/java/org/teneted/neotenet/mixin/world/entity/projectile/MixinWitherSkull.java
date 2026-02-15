package org.teneted.neotenet.mixin.world.entity.projectile;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WitherSkull.class)
public abstract class MixinWitherSkull extends AbstractHurtingProjectile {

    protected MixinWitherSkull(EntityType<? extends AbstractHurtingProjectile> p_36833_, Level p_36834_) {
        super(p_36833_, p_36834_);
    }

    @Inject(method = "onHitEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;heal(F)V"))
    private void neotenet$healReason(EntityHitResult p_37626_, CallbackInfo ci, @Local LivingEntity livingentity) {
        livingentity.pushHealReason(EntityRegainHealthEvent.RegainReason.WITHER);
    }

    @Inject(method = "onHitEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z"))
    private void neotenet$effectCause(EntityHitResult p_37626_, CallbackInfo ci, @Local LivingEntity livingentity) {
        livingentity.pushEffectCause(EntityPotionEffectEvent.Cause.ATTACK);
    }

    @WrapOperation(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;explode(Lnet/minecraft/world/entity/Entity;DDDFZLnet/minecraft/world/level/Level$ExplosionInteraction;)Lnet/minecraft/world/level/Explosion;"))
    private Explosion neotenet$callExplosionPrimeEvent(Level instance, Entity entity, double x, double y, double z, float v, boolean b, Level.ExplosionInteraction explosionInteraction, Operation<Explosion> original) {
        ExplosionPrimeEvent event = new ExplosionPrimeEvent(this.getBukkitEntity(), 1.0F, false);
        this.level().getCraftServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            original.call(instance, ((WitherSkull) (Object) this), x, y, z, event.getRadius(), event.getFire(), explosionInteraction);
        }
        // CraftBukkit end
        this.pushRemoveCause(EntityRemoveEvent.Cause.HIT); // CraftBukkit - add Bukkit remove cause
        return null;
    }
}
