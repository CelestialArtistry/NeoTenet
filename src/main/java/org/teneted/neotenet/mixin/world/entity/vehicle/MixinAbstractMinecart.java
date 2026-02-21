package org.teneted.neotenet.mixin.world.entity.vehicle;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.util.Vector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.teneted.neotenet.injection.world.entity.vehicle.InjectionAbstractMinecart;

@Mixin(AbstractMinecart.class)
public abstract class MixinAbstractMinecart extends VehicleEntity implements InjectionAbstractMinecart {

    @Shadow
    protected abstract double getMaxSpeed();

    @Shadow
    private double derailedX;

    @Shadow
    private double derailedY;

    @Shadow
    private double derailedZ;

    @Shadow
    private double flyingX;

    @Shadow
    private double flyingY;

    @Shadow
    private double flyingZ;

    @Shadow
    public boolean slowWhenEmpty;

    public MixinAbstractMinecart(EntityType<?> p_306130_, Level p_306167_) {
        super(p_306130_, p_306167_);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void neotenet$setValue(CallbackInfo ci) {
        // CraftBukkit start
        double prevX = this.getX();
        double prevY = this.getY();
        double prevZ = this.getZ();
        float prevYaw = this.getYRot();
        float prevPitch = this.getXRot();
        // CraftBukkit end
    }

    @ModifyExpressionValue(method = "applyNaturalSlowdown", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/AbstractMinecart;isVehicle()Z"))
    private boolean neotenet$checkSlowWhenEmpty(boolean original) {
        return original || !this.slowWhenEmpty;
    }

    @Inject(method = "push", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/AbstractMinecart;hasPassenger(Lnet/minecraft/world/entity/Entity;)Z", shift = At.Shift.AFTER), cancellable = true)
    private void neotenet$callVehicleEntityCollisionEvent(Entity p_38165_, CallbackInfo ci) {
        // CraftBukkit start
        VehicleEntityCollisionEvent collisionEvent = new VehicleEntityCollisionEvent((Vehicle) this.getBukkitEntity(), p_38165_.getBukkitEntity());
        this.level().getCraftServer().getPluginManager().callEvent(collisionEvent);

        if (collisionEvent.isCancelled()) {
            ci.cancel();
            return;
        }
        // CraftBukkit end
    }

    // CraftBukkit start
    @Override
    public Vec3 getKnownMovement() {
        double d0 = this.getMaxSpeed();
        Vec3 vec3d = super.getKnownMovement();

        return new Vec3(Mth.clamp(vec3d.x, -d0, d0), vec3d.y, Mth.clamp(vec3d.z, -d0, d0));
    }
    // CraftBukkit end

    // CraftBukkit start - Methods for getting and setting flying and derailed velocity modifiers
    @Override
    public Vector getFlyingVelocityMod() {
        return new Vector(flyingX, flyingY, flyingZ);
    }

    @Override
    public void setFlyingVelocityMod(Vector flying) {
        flyingX = flying.getX();
        flyingY = flying.getY();
        flyingZ = flying.getZ();
    }

    @Override
    public Vector getDerailedVelocityMod() {
        return new Vector(derailedX, derailedY, derailedZ);
    }

    @Override
    public void setDerailedVelocityMod(Vector derailed) {
        derailedX = derailed.getX();
        derailedY = derailed.getY();
        derailedZ = derailed.getZ();
    }
    // CraftBukkit end
}
