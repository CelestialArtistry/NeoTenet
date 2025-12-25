package org.teneted.neotenet.mixin.world.item;

import com.llamalad7.mixinextras.sugar.Cancellable;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ProjectileWeaponItem.class)
public class MixinProjectileWeaponItem {

    @Redirect(method = "shoot", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean neotenet$callEntityShootBowEvent(ServerLevel instance, Entity p_8837_, @Cancellable CallbackInfo ci,
                                                     @Local(ordinal = 0, argsOnly = true) LivingEntity p_330728_,
                                                     @Local(argsOnly = true) InteractionHand p_331152_,
                                                     @Local(ordinal = 0) ItemStack p_330646_,
                                                     @Local(ordinal = 1) ItemStack itemstack,
                                                     @Local(ordinal = 0, argsOnly = true) float p_331007_) {
        // CraftBukkit start
        org.bukkit.event.entity.EntityShootBowEvent event = org.bukkit.craftbukkit.event.CraftEventFactory.callEntityShootBowEvent(p_330728_, p_330646_, itemstack, p_8837_, p_331152_, p_331007_, true);
        if (event.isCancelled()) {
            event.getProjectile().remove();
            ci.cancel();
            return false;
        }

        if (event.getProjectile() == p_8837_.getBukkitEntity()) {
            if (!instance.addFreshEntity(p_8837_)) {
                if (p_330728_ instanceof net.minecraft.server.level.ServerPlayer) {
                    ((net.minecraft.server.level.ServerPlayer) p_330728_).getBukkitEntity().updateInventory();
                }
                ci.cancel();
                return false;
            }
        }
        // CraftBukkit end
        return true;
    }
}
