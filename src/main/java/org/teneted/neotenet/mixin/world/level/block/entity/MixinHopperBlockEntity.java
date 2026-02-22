package org.teneted.neotenet.mixin.world.level.block.entity;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Cancellable;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.inventory.HopperInventorySearchEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(HopperBlockEntity.class)
public abstract class MixinHopperBlockEntity extends RandomizableContainerBlockEntity implements Hopper {

    @Shadow
    private NonNullList<ItemStack> items;

    @Shadow
    public static ItemStack addItem(@Nullable Container p_59327_, Container p_59328_, ItemStack p_59329_, @Nullable Direction p_59330_) {
        return null;
    }

    @Shadow
    @Nullable
    public static Container getContainerAt(Level p_59391_, BlockPos p_59392_) {
        return null;
    }

    @Shadow
    @Nullable
    protected static Container getContainerAt(Level p_59348_, BlockPos p_326114_, BlockState p_326445_, double p_59349_, double p_59350_, double p_59351_) {
        return null;
    }

    // CraftBukkit start - add fields and methods
    public List<HumanEntity> transaction = new java.util.ArrayList<HumanEntity>();
    private int maxStack = MAX_STACK;

    protected MixinHopperBlockEntity(BlockEntityType<?> p_155629_, BlockPos p_155630_, BlockState p_155631_) {
        super(p_155629_, p_155630_, p_155631_);
    }

    @Override
    public List<ItemStack> getContents() {
        return this.items;
    }

    @Override
    public void onOpen(CraftHumanEntity who) {
        transaction.add(who);
    }

    @Override
    public void onClose(CraftHumanEntity who) {
        transaction.remove(who);
    }

    @Override
    public List<HumanEntity> getViewers() {
        return transaction;
    }

    @Override
    public int getMaxStackSize() {
        return maxStack;
    }

    @Override
    public void setMaxStackSize(int size) {
        maxStack = size;
    }
    // CraftBukkit end

    @Redirect(method = "ejectItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/HopperBlockEntity;addItem(Lnet/minecraft/world/Container;Lnet/minecraft/world/Container;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/core/Direction;)Lnet/minecraft/world/item/ItemStack;"))
    private static ItemStack neotenet$callInventoryMoveItemEvent(Container tileentityhopper, Container iinventory, ItemStack itemStack, Direction direction, @Local(argsOnly = true) Level p_155563_, @Local(argsOnly = true) HopperBlockEntity p_326256_, @Local(ordinal = 0) int i, @Cancellable CallbackInfoReturnable<Boolean> cir) {
        // CraftBukkit start - Call event when pushing items into other inventories
        ItemStack original = itemStack.copy();
        CraftItemStack oitemstack = CraftItemStack.asCraftMirror(p_326256_.removeItem(i, 1));

        Inventory destinationInventory;
        // Have to special case large chests as they work oddly
        if (iinventory instanceof CompoundContainer) {
            destinationInventory = new org.bukkit.craftbukkit.inventory.CraftInventoryDoubleChest((CompoundContainer) iinventory);
        } else if (iinventory.getOwner() != null) {
            destinationInventory = iinventory.getOwner().getInventory();
        } else {
            destinationInventory = new CraftInventory(iinventory);
        }

        InventoryMoveItemEvent event = new InventoryMoveItemEvent(p_326256_.getOwner().getInventory(), oitemstack, destinationInventory, true);
        p_155563_.getCraftServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            p_326256_.setItem(i, original);
            p_326256_.setCooldown(8); // Delay hopper checks
            cir.setReturnValue(false);
        }
        return addItem(tileentityhopper, iinventory, CraftItemStack.asNMSCopy(event.getItem()), direction);
    }

    @Redirect(method = "tryTakeInItemFromSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/HopperBlockEntity;addItem(Lnet/minecraft/world/Container;Lnet/minecraft/world/Container;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/core/Direction;)Lnet/minecraft/world/item/ItemStack;"))
    private static ItemStack neotenet$callInventoryMoveItemEvent0(Container iinventory, Container ihopper, ItemStack itemStack, Direction direction, @Local(ordinal = 0) int i, @Cancellable CallbackInfoReturnable<Boolean> cir) {
        // CraftBukkit start - Call event on collection of items from inventories into the hopper
        ItemStack original = itemStack.copy();
        CraftItemStack oitemstack = CraftItemStack.asCraftMirror(iinventory.removeItem(i, 1));

        Inventory sourceInventory;
        // Have to special case large chests as they work oddly
        if (iinventory instanceof CompoundContainer) {
            sourceInventory = new org.bukkit.craftbukkit.inventory.CraftInventoryDoubleChest((CompoundContainer) iinventory);
        } else if (iinventory.getOwner() != null) {
            sourceInventory = iinventory.getOwner().getInventory();
        } else {
            sourceInventory = new CraftInventory(iinventory);
        }

        InventoryMoveItemEvent event = new InventoryMoveItemEvent(sourceInventory, oitemstack, ihopper.getOwner().getInventory(), false);

        Bukkit.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            iinventory.setItem(i, original);

            if (ihopper instanceof HopperBlockEntity) {
                ((HopperBlockEntity) ihopper).setCooldown(8); // Delay hopper checks
            }

            cir.setReturnValue(false);
        }
        return addItem(iinventory, ihopper, CraftItemStack.asNMSCopy(event.getItem()), null);
    }

    @Inject(method = "addItem(Lnet/minecraft/world/Container;Lnet/minecraft/world/entity/item/ItemEntity;)Z", at = @At("HEAD"), cancellable = true)
    private static void neotenet$callInventoryPickupItemEvent(Container p_59332_, ItemEntity p_59333_, CallbackInfoReturnable<Boolean> cir) {
        // CraftBukkit start
        InventoryPickupItemEvent event = new InventoryPickupItemEvent(p_59332_.getOwner().getInventory(), (org.bukkit.entity.Item) p_59333_.getBukkitEntity());
        p_59333_.level().getCraftServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            cir.setReturnValue(false);
        }
        // CraftBukkit end
    }

    @Inject(method = "addItem(Lnet/minecraft/world/Container;Lnet/minecraft/world/entity/item/ItemEntity;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;discard()V"))
    private static void neotenet$removeCause(Container p_59332_, ItemEntity p_59333_, CallbackInfoReturnable<Boolean> cir) {
        p_59333_.pushRemoveCause(EntityRemoveEvent.Cause.PICKUP);
    }

    // CraftBukkit start
    @Nullable
    private static Container runHopperInventorySearchEvent(Container inventory, CraftBlock hopper, CraftBlock searchLocation, HopperInventorySearchEvent.ContainerType containerType) {
        HopperInventorySearchEvent event = new HopperInventorySearchEvent((inventory != null) ? new CraftInventory(inventory) : null, containerType, hopper, searchLocation);
        Bukkit.getServer().getPluginManager().callEvent(event);
        CraftInventory craftInventory = (CraftInventory) event.getInventory();
        return (craftInventory != null) ? craftInventory.getInventory() : null;
    }
    // CraftBukkit end

    @ModifyReturnValue(method = "getAttachedContainer", at = @At("RETURN"))
    private static Container neotenet$callHopperInventorySearchEvent(Container original, @Local(argsOnly = true) Level p_155593_, @Local(argsOnly = true) BlockPos p_155594_, @Local(argsOnly = true) HopperBlockEntity p_326320_) {
        // CraftBukkit start
        BlockPos searchPosition = p_155594_.relative(p_326320_.getFacing());
        Container inventory = getContainerAt(p_155593_, searchPosition);

        CraftBlock hopper = CraftBlock.at(p_155593_, p_155594_);
        CraftBlock searchBlock = CraftBlock.at(p_155593_, searchPosition);
        return runHopperInventorySearchEvent(inventory, hopper, searchBlock, HopperInventorySearchEvent.ContainerType.DESTINATION);
    }

    @ModifyReturnValue(method = "getSourceContainer", at = @At("RETURN"))
    private static Container neotenet$runHopperInventorySearchEvent(Container original, @Local(argsOnly = true) Level p_155597_, @Local(argsOnly = true) Hopper p_155598_, @Local(argsOnly = true) BlockPos p_326315_, BlockState p_326093_) {
        // CraftBukkit start
        Container inventory = getContainerAt(p_155597_, p_326315_, p_326093_, p_155598_.getLevelX(), p_155598_.getLevelY() + 1.0D, p_155598_.getLevelZ());

        BlockPos blockPosition = BlockPos.containing(p_155598_.getLevelX(), p_155598_.getLevelY(), p_155598_.getLevelZ());
        CraftBlock hopper = CraftBlock.at(p_155597_, blockPosition);
        CraftBlock container = CraftBlock.at(p_155597_, blockPosition.above());
        return runHopperInventorySearchEvent(inventory, hopper, container, HopperInventorySearchEvent.ContainerType.SOURCE);
        // CraftBukkit end
    }
}
