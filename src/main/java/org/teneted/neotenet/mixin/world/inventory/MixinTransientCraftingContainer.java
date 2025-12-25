package org.teneted.neotenet.mixin.world.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.teneted.neotenet.injection.world.inventory.InjectionTransientCraftingContainer;

import java.util.List;

@Mixin(TransientCraftingContainer.class)
public abstract class MixinTransientCraftingContainer implements CraftingContainer, InjectionTransientCraftingContainer {

    @Shadow
    public List<HumanEntity> transaction = new java.util.ArrayList<HumanEntity>();
    private RecipeHolder<?> currentRecipe;
    @Shadow
    public Container resultInventory;
    @Shadow
    private Player owner;
    @Shadow
    private int maxStack = MAX_STACK;

    @Shadow
    @Final
    private NonNullList<ItemStack> items;

    @Shadow
    @Final
    private AbstractContainerMenu menu;

    public List<ItemStack> getContents() {
        return this.items;
    }

    public void onOpen(CraftHumanEntity who) {
        transaction.add(who);
    }

    public InventoryType getInvType() {
        return items.size() == 4 ? InventoryType.CRAFTING : InventoryType.WORKBENCH;
    }

    public void onClose(CraftHumanEntity who) {
        transaction.remove(who);
    }

    public List<HumanEntity> getViewers() {
        return transaction;
    }

    public org.bukkit.inventory.InventoryHolder getOwner() {
        return (owner == null) ? null : owner.getBukkitEntity();
    }

    @Override
    public int getMaxStackSize() {
        return maxStack;
    }

    public void setMaxStackSize(int size) {
        maxStack = size;
        resultInventory.setMaxStackSize(size);
    }

    @Override
    public Location getLocation() {
        return menu instanceof CraftingMenu ? ((CraftingMenu) menu).access.getLocation() : owner.getBukkitEntity().getLocation();
    }

    @Override
    public RecipeHolder<?> getCurrentRecipe() {
        return currentRecipe;
    }

    @Override
    public void setCurrentRecipe(RecipeHolder<?> currentRecipe) {
        this.currentRecipe = currentRecipe;
    }
}
