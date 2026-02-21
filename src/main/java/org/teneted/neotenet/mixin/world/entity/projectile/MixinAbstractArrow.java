package org.teneted.neotenet.mixin.world.entity.projectile;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Cancellable;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArrow.class)
public abstract class MixinAbstractArrow extends Projectile {

    @Shadow
    protected abstract ItemStack getPickupItem();

    @Shadow
    public AbstractArrow.Pickup pickup;

    protected MixinAbstractArrow(EntityType<? extends Projectile> p_37248_, Level p_37249_) {
        super(p_37248_, p_37249_);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;hitTargetOrDeflectSelf(Lnet/minecraft/world/phys/HitResult;)Lnet/minecraft/world/entity/projectile/ProjectileDeflection;"))
    private ProjectileDeflection neotenet$usePreHitTargetOrDeflectSelf(AbstractArrow instance, HitResult hitResult) {
        return this.preHitTargetOrDeflectSelf(hitResult);
    }

    @Inject(method = "tickDespawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;discard()V"))
    private void neotenet$discardReason(CallbackInfo ci) {
        this.pushRemoveCause(EntityRemoveEvent.Cause.DESPAWN);
    }

    @Inject(method = "onHitEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;discard()V"))
    private void neotenet$discardHitReason(EntityHitResult p_36757_, CallbackInfo ci) {
        this.pushRemoveCause(EntityRemoveEvent.Cause.HIT);
    }

    @ModifyArg(method = "onHitEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;igniteForSeconds(F)V"), index = 0)
    private float neotenet$callEntityCombustByEntityEvent(float p_345382_, @Cancellable CallbackInfo ci, @Local(ordinal = 0) Entity entity) {
        // CraftBukkit start
        EntityCombustByEntityEvent combustEvent = new EntityCombustByEntityEvent(this.getBukkitEntity(), entity.getBukkitEntity(), 5.0F);
        org.bukkit.Bukkit.getPluginManager().callEvent(combustEvent);
        if (combustEvent.isCancelled()) {
            ci.cancel();
        }
        // CraftBukkit end
        return combustEvent.getDuration();
    }

    @Inject(method = "playerTouch", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;tryPickup(Lnet/minecraft/world/entity/player/Player;)Z"), cancellable = true)
    private void neotenet$callPlayerPickupArrowEvent(Player entityhuman, CallbackInfo ci, @Share("itemstack") LocalRef<ItemStack> itemStackLocalRef) {
        // CraftBukkit start
        ItemStack itemstack = this.getPickupItem();
        if (this.pickup == AbstractArrow.Pickup.ALLOWED && !itemstack.isEmpty() && entityhuman.getInventory().canHold(itemstack) > 0) {
            ItemEntity item = new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), itemstack);
            PlayerPickupArrowEvent event = new PlayerPickupArrowEvent((org.bukkit.entity.Player) entityhuman.getBukkitEntity(), new org.bukkit.craftbukkit.entity.CraftItem(this.level().getCraftServer(), item), (org.bukkit.entity.AbstractArrow) this.getBukkitEntity());
            // event.setCancelled(!entityhuman.canPickUpLoot); TODO
            this.level().getCraftServer().getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                ci.cancel();
                return;
            }
            itemstack = item.getItem();
            itemStackLocalRef.set(itemstack);
        }
    }

    @ModifyExpressionValue(method = "playerTouch", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;tryPickup(Lnet/minecraft/world/entity/player/Player;)Z"))
    private boolean neotenet$resetCheckPickup(boolean original, @Local(argsOnly = true) Player entityhuman, @Share("itemstack") LocalRef<ItemStack> itemStackLocalRef) {
        return (this.pickup == AbstractArrow.Pickup.ALLOWED && entityhuman.getInventory().add(itemStackLocalRef.get())) || (this.pickup == AbstractArrow.Pickup.CREATIVE_ONLY && entityhuman.getAbilities().instabuild);
    }

    @Inject(method = "playerTouch", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;discard()V"))
    private void neotenet$discardPickupReason(Player p_36766_, CallbackInfo ci) {
        this.pushRemoveCause(EntityRemoveEvent.Cause.PICKUP);
    }
}
