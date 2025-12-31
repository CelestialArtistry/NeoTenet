package org.teneted.neotenet.mixin.world.entity.vehicle;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import net.minecraft.world.level.Level;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VehicleEntity.class)
public abstract class MixinVehicleEntity extends Entity {

    @Shadow
    public abstract void setDamage(float p_306297_);

    public MixinVehicleEntity(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Inject(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/VehicleEntity;setHurtDir(I)V"), cancellable = true)
    private void neotenet$callVehicleDamageEvent(DamageSource damagesource, float f, CallbackInfoReturnable<Boolean> cir, @Share("attacker") LocalRef<org.bukkit.entity.Entity> attacker, @Share("vehicle") LocalRef<Vehicle> vehicle) {
        // CraftBukkit start
        vehicle.set((Vehicle)this.getBukkitEntity());
        attacker.set((damagesource.getEntity() == null) ? null : damagesource.getEntity().getBukkitEntity());

        VehicleDamageEvent event = new VehicleDamageEvent(vehicle.get(), attacker.get(), (double) f);
        this.level().getCraftServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            cir.setReturnValue(false);
        }
        f = (float) event.getDamage();
        // CraftBukkit end
    }

    @Inject(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/VehicleEntity;discard()V"), cancellable = true)
    private void neotenet$callVehicleDestroyEvent(DamageSource p_305898_, float p_305999_, CallbackInfoReturnable<Boolean> cir, @Share("attacker") LocalRef<org.bukkit.entity.Entity> attacker, @Share("vehicle") LocalRef<Vehicle> vehicle) {
        // CraftBukkit start
        VehicleDestroyEvent destroyEvent = new VehicleDestroyEvent(vehicle.get(), attacker.get());
        this.level().getCraftServer().getPluginManager().callEvent(destroyEvent);

        if (destroyEvent.isCancelled()) {
            this.setDamage(40.0F); // Maximize damage so this doesn't get triggered again right away
            cir.setReturnValue(true);
        }
        // CraftBukkit end
        this.pushRemoveCause(EntityRemoveEvent.Cause.DEATH);
    }

    @Inject(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/VehicleEntity;destroy(Lnet/minecraft/world/damagesource/DamageSource;)V"), cancellable = true)
    private void neotenet$callVehicleDestroyEvent0(DamageSource p_305898_, float p_305999_, CallbackInfoReturnable<Boolean> cir, @Share("attacker") LocalRef<org.bukkit.entity.Entity> attacker, @Share("vehicle") LocalRef<Vehicle> vehicle) {
        // CraftBukkit start
        VehicleDestroyEvent destroyEvent = new VehicleDestroyEvent(vehicle.get(), attacker.get());
        this.level().getCraftServer().getPluginManager().callEvent(destroyEvent);

        if (destroyEvent.isCancelled()) {
            this.setDamage(40.0F); // Maximize damage so this doesn't get triggered again right away
            cir.setReturnValue(true);
        }
        // CraftBukkit end
    }
}
