package org.teneted.neotenet.mixin.world.entity.boss.enderdragon;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Cancellable;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EndCrystal.class)
public abstract class MixinEndCrystal extends Entity {

    public MixinEndCrystal(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private boolean neotenet$callBlockIgniteEvent(Level instance, BlockPos p_46598_, BlockState p_46599_, Operation<Boolean> original) {
        if (!CraftEventFactory.callBlockIgniteEvent(instance, p_46598_, ((EndCrystal) (Object) this)).isCancelled()) {
            return original.call(instance, p_46598_, p_46599_);
        } else {
            return false;
        }
    }

    @Redirect(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/boss/enderdragon/EndCrystal;remove(Lnet/minecraft/world/entity/Entity$RemovalReason;)V"))
    private void neotenet$handleNonLivingEntityDamageEvent(EndCrystal instance, Entity.RemovalReason removalReason, @Local(argsOnly = true) DamageSource p_31050_, @Local(argsOnly = true) float p_31051_, @Cancellable CallbackInfoReturnable<Boolean> cir) {
        // CraftBukkit start - All non-living entities need this
        if (CraftEventFactory.handleNonLivingEntityDamageEvent(instance, p_31050_, p_31051_, false)) {
            cir.setReturnValue(false);
        }
        // CraftBukkit end
    }

    @Redirect(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;explode(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;Lnet/minecraft/world/level/ExplosionDamageCalculator;DDDFZLnet/minecraft/world/level/Level$ExplosionInteraction;)Lnet/minecraft/world/level/Explosion;"))
    private Explosion neotenet$callExplosionPrimeEvent(Level instance, Entity p_256145_, DamageSource p_256004_, ExplosionDamageCalculator p_255696_, double p_256208_, double p_256036_, double p_255746_, float p_256647_, boolean p_256098_, Level.ExplosionInteraction p_256104_, @Cancellable CallbackInfoReturnable<Boolean> cir) {
        // CraftBukkit start
        ExplosionPrimeEvent event = CraftEventFactory.callExplosionPrimeEvent(this, 6.0F, false);
        if (event.isCancelled()) {
            cir.setReturnValue(false);
        }

        this.remove(Entity.RemovalReason.KILLED, EntityRemoveEvent.Cause.EXPLODE); // CraftBukkit - add Bukkit remove cause
        return this.level().explode(this, p_256004_, (ExplosionDamageCalculator) null, this.getX(), this.getY(), this.getZ(), event.getRadius(), event.getFire(), Level.ExplosionInteraction.BLOCK);
    }
}
