package org.teneted.neotenet.mixin.world.level;

import com.llamalad7.mixinextras.sugar.Cancellable;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Explosion.class)
public class MixinExplosion {

    @Mutable
    @Shadow
    @Final
    private float radius;

    @Shadow
    public float yield;

    @Shadow
    @Final
    private Explosion.BlockInteraction blockInteraction;

    @Shadow
    @Final
    private DamageSource damageSource;

    @Shadow
    @Final
    private ExplosionDamageCalculator damageCalculator;

    @Redirect(method = "<init>(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;Lnet/minecraft/world/level/ExplosionDamageCalculator;DDDFZLnet/minecraft/world/level/Explosion$BlockInteraction;Lnet/minecraft/core/particles/ParticleOptions;Lnet/minecraft/core/particles/ParticleOptions;Lnet/minecraft/core/Holder;)V",
            at = @At(value = "FIELD",
                    target = "Lnet/minecraft/world/level/Explosion;radius:F"))
    private void neotenet$resetRadius(Explosion explosion, float radius, @Local(argsOnly = true) float p_46029_) {
        this.radius = (float) Math.max(p_46029_, 0.0); // CraftBukkit - clamp bad values
    }

    @Inject(method = "<init>(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;Lnet/minecraft/world/level/ExplosionDamageCalculator;DDDFZLnet/minecraft/world/level/Explosion$BlockInteraction;Lnet/minecraft/core/particles/ParticleOptions;Lnet/minecraft/core/particles/ParticleOptions;Lnet/minecraft/core/Holder;)V",
            at = @At("RETURN"))
    private void neotenet$setYield(Level p_46024_, Entity p_46025_, DamageSource p_312268_, ExplosionDamageCalculator p_312205_, double p_46026_, double p_46027_, double p_46028_, float p_46029_, boolean p_312333_, Explosion.BlockInteraction p_312294_, ParticleOptions p_312158_, ParticleOptions p_311904_, Holder p_320270_, CallbackInfo ci) {
        this.yield = this.blockInteraction == Explosion.BlockInteraction.DESTROY_WITH_DECAY ? 1.0F / this.radius : 1.0F; // CraftBukkit
    }

    @Inject(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;gameEvent(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/Holder;Lnet/minecraft/world/phys/Vec3;)V"), cancellable = true)
    private void neotenet$checkRadius(CallbackInfo ci) {
        // CraftBukkit start
        if (this.radius < 0.1F) {
            ci.cancel();
            return;
        }
        // CraftBukkit end
    }

    @Redirect(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private boolean neotenet$setLastDamage(Entity instance, DamageSource damageSource, float v, @Cancellable CallbackInfo ci, @Local List<Entity> list) {
        // CraftBukkit start

        // Special case ender dragon only give knockback if no damage is cancelled
        // Thinks to note:
        // - Setting a velocity to a ComplexEntityPart is ignored (and therefore not needed)
        // - Damaging ComplexEntityPart while forward the damage to EntityEnderDragon
        // - Damaging EntityEnderDragon does nothing
        // - EntityEnderDragon hitbock always covers the other parts and is therefore always present
        if (!(instance instanceof EnderDragonPart)) {
            ci.cancel();
        }

        instance.lastDamageCancelled = false;

        if (instance instanceof EnderDragon) {
            for (EnderDragonPart entityComplexPart : ((EnderDragon) instance).subEntities) {
                // Calculate damage separately for each EntityComplexPart
                if (list.contains(entityComplexPart)) {
                    entityComplexPart.hurt(this.damageSource, this.damageCalculator.getEntityDamageAmount(((Explosion) (Object) this), instance));
                }
            }
        } else {
            instance.hurt(this.damageSource, this.damageCalculator.getEntityDamageAmount(((Explosion) (Object) this), instance));
        }

        if (!(instance.lastDamageCancelled)) { // SPIGOT-5339, SPIGOT-6252, SPIGOT-6777: Skip entity if damage event was cancelled
            ci.cancel();
        }
        // CraftBukkit end
        return true;
    }
}
