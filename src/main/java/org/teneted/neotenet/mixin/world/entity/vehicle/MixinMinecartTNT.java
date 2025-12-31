package org.teneted.neotenet.mixin.world.entity.vehicle;

import com.llamalad7.mixinextras.sugar.Cancellable;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.MinecartTNT;
import net.minecraft.world.level.Level;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(MinecartTNT.class)
public abstract class MixinMinecartTNT extends AbstractMinecart {

    @Shadow
    private int fuse;

    protected MixinMinecartTNT(EntityType<?> p_38087_, Level p_38088_) {
        super(p_38087_, p_38088_);
    }

    @ModifyArgs(method = "explode(Lnet/minecraft/world/damagesource/DamageSource;D)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;explode(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;Lnet/minecraft/world/level/ExplosionDamageCalculator;DDDFZLnet/minecraft/world/level/Level$ExplosionInteraction;)Lnet/minecraft/world/level/Explosion;"))
    private void neotenet$callExplosionPrimeEvent(Args args, @Cancellable CallbackInfo ci, @Local(ordinal = 1) double d0) {
        // CraftBukkit start
        ExplosionPrimeEvent event = new ExplosionPrimeEvent(this.getBukkitEntity(), (float) (4.0D + this.random.nextDouble() * 1.5D * d0), false);
        this.level().getCraftServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            fuse = -1;
            ci.cancel();
            return;
        }
        args.set(6, event.getRadius());
        args.set(7, event.getFire());
    }

    @Inject(method = "explode(Lnet/minecraft/world/damagesource/DamageSource;D)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/MinecartTNT;discard()V"))
    private void neotenet$removeCause(DamageSource p_259539_, double p_260287_, CallbackInfo ci) {
        this.pushRemoveCause(EntityRemoveEvent.Cause.EXPLODE);
    }
}
