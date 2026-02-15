package org.teneted.neotenet.mixin.world.entity.projectile;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.bukkit.projectiles.ProjectileSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.teneted.neotenet.injection.world.entity.projectile.InjectionProjectile;

@Mixin(Projectile.class)
public abstract class MixinProjectile extends Entity implements InjectionProjectile {

    @Shadow
    protected abstract ProjectileDeflection hitTargetOrDeflectSelf(HitResult p_341949_);

    // CraftBukkit start
    @Unique
    private boolean hitCancelled = false;
    // CraftBukkit end

    public MixinProjectile(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Inject(method = "setOwner", at = @At("RETURN"))
    private void neotenet$initOwner(Entity entity, CallbackInfo ci) {
        this.projectileSource = (entity != null && entity.getBukkitEntity() instanceof ProjectileSource) ? (ProjectileSource) entity.getBukkitEntity() : null; // CraftBukkit
    }

    // CraftBukkit start - call projectile hit event
    @Override
    public ProjectileDeflection preHitTargetOrDeflectSelf(HitResult movingobjectposition) {
        org.bukkit.event.entity.ProjectileHitEvent event = org.bukkit.craftbukkit.event.CraftEventFactory.callProjectileHitEvent(this, movingobjectposition);
        this.hitCancelled = event != null && event.isCancelled();
        if (movingobjectposition.getType() == HitResult.Type.BLOCK || !this.hitCancelled) {
            return this.hitTargetOrDeflectSelf(movingobjectposition);
        }
        return ProjectileDeflection.NONE;
    }
    // CraftBukkit end

    @Inject(method = "onHitBlock", at = @At("HEAD"), cancellable = true)
    private void neotenet$checkHit(BlockHitResult p_37258_, CallbackInfo ci) {
        // CraftBukkit start - cancellable hit event
        if (hitCancelled) {
            ci.cancel();
            return;
        }
        // CraftBukkit end
    }
}
