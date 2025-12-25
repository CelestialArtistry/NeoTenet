package org.teneted.neotenet.mixin.world.entity.animal;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.player.PlayerBucketEntityEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(Bucketable.class)
public interface MixinBucketable {

    @Redirect(method = "bucketMobPickup", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;playSound(Lnet/minecraft/sounds/SoundEvent;FF)V"))
    private static void neotenet$cancelPlaySound(LivingEntity instance, SoundEvent soundEvent, float x, float y) {
    }

    @Inject(method = "bucketMobPickup", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemUtils;createFilledResult(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;Z)Lnet/minecraft/world/item/ItemStack;"))
    private static <T extends LivingEntity & Bucketable> void neotenet$bucketFishEvent(Player p_148829_, InteractionHand p_148830_, T p_148831_, CallbackInfoReturnable<Optional<InteractionResult>> cir, @Local(ordinal = 0) ItemStack itemstack, @Local(ordinal = 1) ItemStack itemstack2) {
        PlayerBucketEntityEvent playerBucketFishEvent = CraftEventFactory.callPlayerFishBucketEvent(p_148831_, p_148829_, itemstack, itemstack2, p_148830_);
        itemstack2 = CraftItemStack.asNMSCopy(playerBucketFishEvent.getEntityBucket());
        if (playerBucketFishEvent.isCancelled()) {
            ((ServerPlayer) p_148829_).containerMenu.sendAllDataToRemote(); // We need to update inventory to resync client's bucket
            p_148831_.getBukkitEntity().update((ServerPlayer) p_148829_); // We need to play out these packets as the client assumes the fish is gone
            p_148831_.refreshEntityData((ServerPlayer) p_148829_); // Need to send data such as the display name to client
            cir.setReturnValue(Optional.of(InteractionResult.FAIL));
        }
        p_148831_.playSound(((Bucketable) p_148831_).getPickupSound(), 1.0F, 1.0F);
        // CraftBukkit end
    }

    @Inject(method = "bucketMobPickup", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;discard()V"))
    private static <T extends LivingEntity & Bucketable> void neotenet$removeCause(Player p_148829_, InteractionHand p_148830_, T p_148831_, CallbackInfoReturnable<Optional<InteractionResult>> cir) {
        p_148831_.pushRemoveCause(EntityRemoveEvent.Cause.PICKUP); // CraftBukkit - add Bukkit remove cause
    }
}
