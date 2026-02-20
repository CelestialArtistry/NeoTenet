package org.teneted.neotenet.mixin.world.entity.projectile;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.bukkit.entity.Explosive;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(LargeFireball.class)
public abstract class MixinLargeFireball extends Fireball {

    @Shadow
    private int explosionPower;

    public MixinLargeFireball(EntityType<? extends Fireball> p_37006_, Level p_37007_) {
        super(p_37006_, p_37007_);
    }

    @Inject(method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;)V", at = @At("RETURN"))
    private void neotenet$initA(EntityType p_37199_, Level p_37200_, CallbackInfo ci) {
        isIncendiary = this.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING); // CraftBukkit
    }

    @Inject(method = "<init>(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/phys/Vec3;I)V", at = @At("RETURN"))
    private void neotenet$initB(Level p_181151_, LivingEntity p_181152_, Vec3 p_347580_, int p_181156_, CallbackInfo ci) {
        isIncendiary = this.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING); // CraftBukkit
    }

    @WrapOperation(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;explode(Lnet/minecraft/world/entity/Entity;DDDFZLnet/minecraft/world/level/Level$ExplosionInteraction;)Lnet/minecraft/world/level/Explosion;"))
    private Explosion neotenet$callExplodeEvent(Level instance, Entity entity, double x, double y, double z, float v, boolean b, Level.ExplosionInteraction explosionInteraction, Operation<Explosion> original, @Share("bukkitEvent") LocalRef<ExplosionPrimeEvent> bukkitEvent) {
        // CraftBukkit start - fire ExplosionPrimeEvent
        ExplosionPrimeEvent event = new ExplosionPrimeEvent((Explosive) this.getBukkitEntity());
        bukkitEvent.set(event);
        this.level().getCraftServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            return original.call(instance, entity, x, y, z, v, b, explosionInteraction);
        } else {
            return null;
        }
    }

    @ModifyArgs(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;explode(Lnet/minecraft/world/entity/Entity;DDDFZLnet/minecraft/world/level/Level$ExplosionInteraction;)Lnet/minecraft/world/level/Explosion;"))
    private void neotenet$useEventThings(Args args, @Share("bukkitEvent") LocalRef<ExplosionPrimeEvent> bukkitEvent) {
        args.set(4, bukkitEvent.get().getFire());
        args.set(5, bukkitEvent.get().getRadius());
        this.pushRemoveCause(EntityRemoveEvent.Cause.HIT);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    private void neotenet$setBukkitYield(CompoundTag p_37220_, CallbackInfo ci) {
        this.bukkitYield = explosionPower;
    }
}
