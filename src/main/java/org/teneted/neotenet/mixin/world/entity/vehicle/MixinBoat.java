package org.teneted.neotenet.mixin.world.entity.vehicle;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import net.minecraft.world.level.Level;
import org.bukkit.Location;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Boat.class)
public abstract class MixinBoat extends VehicleEntity {

    public MixinBoat(EntityType<?> p_306130_, Level p_306167_) {
        super(p_306130_, p_306167_);
    }

    private Location lastLocation; // CraftBukkit

    @Inject(method = "push", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/VehicleEntity;push(Lnet/minecraft/world/entity/Entity;)V", ordinal = 0), cancellable = true)
    private void neotenet$callVehicleEntityCollisionEvent(Entity p_38373_, CallbackInfo ci) {
        // CraftBukkit start
        if (!this.isPassengerOfSameVehicle(p_38373_)) {
            VehicleEntityCollisionEvent event = new VehicleEntityCollisionEvent((Vehicle) this.getBukkitEntity(), p_38373_.getBukkitEntity());
            this.level().getCraftServer().getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                ci.cancel();
                return;
            }
        }
        // CraftBukkit end
    }

    @Inject(method = "push", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/VehicleEntity;push(Lnet/minecraft/world/entity/Entity;)V", ordinal = 1), cancellable = true)
    private void neotenet$callVehicleEntityCollisionEvent0(Entity p_38373_, CallbackInfo ci) {
        // CraftBukkit start
        if (!this.isPassengerOfSameVehicle(p_38373_)) {
            VehicleEntityCollisionEvent event = new VehicleEntityCollisionEvent((Vehicle) this.getBukkitEntity(), p_38373_.getBukkitEntity());
            this.level().getCraftServer().getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                ci.cancel();
                return;
            }
        }
        // CraftBukkit end
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/Boat;tickBubbleColumn()V"))
    private void neotenet$callVehicleMoveEvent(CallbackInfo ci) {
        // CraftBukkit start
        org.bukkit.Server server = this.level().getCraftServer();
        org.bukkit.World bworld = this.level().getWorld();

        Location to = CraftLocation.toBukkit(this.position(), bworld, this.getYRot(), this.getXRot());
        Vehicle vehicle = (Vehicle) this.getBukkitEntity();

        server.getPluginManager().callEvent(new org.bukkit.event.vehicle.VehicleUpdateEvent(vehicle));

        if (lastLocation != null && !lastLocation.equals(to)) {
            VehicleMoveEvent event = new VehicleMoveEvent(vehicle, lastLocation, to);
            server.getPluginManager().callEvent(event);
        }
        lastLocation = vehicle.getLocation();
        // CraftBukkit end
    }

    @Override
    public void remove(Entity.RemovalReason entity_removalreason, EntityRemoveEvent.Cause cause) {
        this.pushRemoveCause(cause);
        this.remove(entity_removalreason);
    }

    @ModifyExpressionValue(method = "checkFallDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/Boat;isRemoved()Z"))
    private boolean neotenet$checkFallDamage(boolean original) {
        // CraftBukkit start
        Vehicle vehicle = (Vehicle) this.getBukkitEntity();
        VehicleDestroyEvent destroyEvent = new VehicleDestroyEvent(vehicle, null);
        this.level().getCraftServer().getPluginManager().callEvent(destroyEvent);
        return original && !destroyEvent.isCancelled();
    }
}
