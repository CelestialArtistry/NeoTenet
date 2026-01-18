package org.teneted.neotenet.mixin.world.level.block;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.CrafterBlock;
import net.minecraft.world.level.block.entity.CrafterBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.block.CrafterCraftEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CrafterBlock.class)
public class MixinCrafterBlock {

    @Inject(method = "dispenseFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z", ordinal = 0), cancellable = true)
    private void neotenet$callCrafterCraftEvent(BlockState p_307495_, ServerLevel p_307310_, BlockPos p_307672_, CallbackInfo ci, @Local ItemStack itemstack, @Local CrafterBlockEntity crafterblockentity, @Local RecipeHolder<CraftingRecipe> recipeholder) {
        // CraftBukkit start
        CrafterCraftEvent event = CraftEventFactory.callCrafterCraftEvent(p_307672_, p_307310_, crafterblockentity, itemstack, recipeholder);
        if (event.isCancelled()) {
            ci.cancel();
            return;
        }
        itemstack = CraftItemStack.asNMSCopy(event.getResult());
        // CraftBukkit end
    }

    @Inject(method = "dispenseItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;copyWithCount(I)Lnet/minecraft/world/item/ItemStack;"), cancellable = true)
    private void neotenet$callInventoryMoveItemEvent(ServerLevel p_335887_, BlockPos p_307620_, CrafterBlockEntity p_307387_, ItemStack p_307296_, BlockState p_307501_, RecipeHolder<CraftingRecipe> p_335494_, CallbackInfo ci, @Local(ordinal = 1) ItemStack itemstack, @Local Container container) {
        // CraftBukkit start - InventoryMoveItemEvent
        CraftItemStack oitemstack = CraftItemStack.asCraftMirror(itemstack);

        Inventory destinationInventory = null;
        // Have to special case large chests as they work oddly
        if (container instanceof CompoundContainer) {
            destinationInventory = new org.bukkit.craftbukkit.inventory.CraftInventoryDoubleChest((CompoundContainer) container);
        } else {
            destinationInventory = container.getOwner().getInventory();
        }

        InventoryMoveItemEvent event = new InventoryMoveItemEvent(p_307387_.getOwner().getInventory(), oitemstack, destinationInventory, true);
        p_335887_.getCraftServer().getPluginManager().callEvent(event);
        itemstack = CraftItemStack.asNMSCopy(event.getItem());
        if (event.isCancelled()) {
            ci.cancel();
        }
        // CraftBukkit end
    }

    @Inject(method = "dispenseItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getCount()I"), cancellable = true)
    private void neotenet$callInventoryMoveItemEvent0(ServerLevel p_335887_, BlockPos p_307620_, CrafterBlockEntity p_307387_, ItemStack p_307296_, BlockState p_307501_, RecipeHolder<CraftingRecipe> p_335494_, CallbackInfo ci, @Local(ordinal = 1) ItemStack itemstack, @Local Container container) {
        // CraftBukkit start - InventoryMoveItemEvent
        CraftItemStack oitemstack = CraftItemStack.asCraftMirror(itemstack);

        Inventory destinationInventory = null;
        // Have to special case large chests as they work oddly
        if (container instanceof CompoundContainer) {
            destinationInventory = new org.bukkit.craftbukkit.inventory.CraftInventoryDoubleChest((CompoundContainer) container);
        } else {
            destinationInventory = container.getOwner().getInventory();
        }

        InventoryMoveItemEvent event = new InventoryMoveItemEvent(p_307387_.getOwner().getInventory(), oitemstack, destinationInventory, true);
        p_335887_.getCraftServer().getPluginManager().callEvent(event);
        itemstack = CraftItemStack.asNMSCopy(event.getItem());
        if (event.isCancelled()) {
            ci.cancel();
        }
        // CraftBukkit end
    }
}
