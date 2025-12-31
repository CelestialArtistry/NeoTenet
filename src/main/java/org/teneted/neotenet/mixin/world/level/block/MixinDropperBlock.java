package org.teneted.neotenet.mixin.world.level.block;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Cancellable;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DropperBlock;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DropperBlock.class)
public class MixinDropperBlock {

    @Mutable
    @Shadow
    @Final
    private static DispenseItemBehavior DISPENSE_BEHAVIOUR;

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void neotenet$init(CallbackInfo ci) {
        DISPENSE_BEHAVIOUR = new DefaultDispenseItemBehavior(true);
    }

    @ModifyExpressionValue(method = "dispenseFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z", ordinal = 1))
    private boolean neotenet$callInventoryMoveItemEvent(boolean original, @Local(argsOnly = true) ServerLevel p_52944_, @Local DispenserBlockEntity dispenserblockentity, @Local Container container, @Local Direction direction, @Local(ordinal = 0) ItemStack itemstack, @Local(ordinal = 1) ItemStack itemstack1, @Cancellable CallbackInfo ci) {
        // CraftBukkit start - Fire event when pushing items into other inventories
        CraftItemStack oitemstack = CraftItemStack.asCraftMirror(itemstack.copyWithCount(1));

        org.bukkit.inventory.Inventory destinationInventory;
        // Have to special case large chests as they work oddly
        if (container instanceof CompoundContainer) {
            destinationInventory = new org.bukkit.craftbukkit.inventory.CraftInventoryDoubleChest((CompoundContainer) container);
        } else {
            destinationInventory = container.getOwner().getInventory();
        }

        InventoryMoveItemEvent event = new InventoryMoveItemEvent(dispenserblockentity.getOwner().getInventory(), oitemstack, destinationInventory, true);
        p_52944_.getCraftServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
        itemstack1 = HopperBlockEntity.addItem(dispenserblockentity, container, CraftItemStack.asNMSCopy(event.getItem()), direction.getOpposite());
        return event.getItem().equals(oitemstack) && original;
    }
}
