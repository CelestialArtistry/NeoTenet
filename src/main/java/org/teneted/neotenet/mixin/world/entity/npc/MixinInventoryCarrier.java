package org.teneted.neotenet.mixin.world.entity.npc;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.item.ItemStack;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryCarrier.class)
public interface MixinInventoryCarrier {

    @Inject(method = "pickUpItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;onItemPickup(Lnet/minecraft/world/entity/item/ItemEntity;)V"), cancellable = true)
    private static void neotenet$callEntityPickupItemEvent(Mob p_219612_, InventoryCarrier p_219613_, ItemEntity p_219614_, CallbackInfo ci, @Local SimpleContainer simplecontainer, @Local ItemStack itemstack) {
        // CraftBukkit start
        ItemStack remaining = new SimpleContainer(simplecontainer).addItem(itemstack);
        if (org.bukkit.craftbukkit.event.CraftEventFactory.callEntityPickupItemEvent(p_219612_, p_219614_, remaining.getCount(), false).isCancelled()) {
            ci.cancel();
            return;
        }
        // CraftBukkit end
    }

    @Inject(method = "pickUpItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;discard()V"))
    private static void neotenet$discardReason(Mob p_219612_, InventoryCarrier p_219613_, ItemEntity p_219614_, CallbackInfo ci) {
        p_219614_.pushRemoveCause(EntityRemoveEvent.Cause.PICKUP);
    }
}
