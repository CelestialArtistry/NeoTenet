package org.teneted.neotenet.mixin.core.world;

import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.List;

@Mixin(CompoundContainer.class)
public abstract class CompoundContainerMixin implements Container {

    @Shadow
    @Final
    public Container container1;
    @Shadow
    @Final
    public Container container2;
    // CraftBukkit start - add fields and methods
    public List<HumanEntity> transaction = new java.util.ArrayList<HumanEntity>();

    @Override
    public List<ItemStack> getContents() {
        List<ItemStack> result = new ArrayList<ItemStack>(this.getContainerSize());
        for (int i = 0; i < this.getContainerSize(); i++) {
            result.add(this.getItem(i));
        }
        return result;
    }

    @Override
    public void onOpen(CraftHumanEntity who) {
        this.container1.onOpen(who);
        this.container2.onOpen(who);
        transaction.add(who);
    }

    @Override
    public void onClose(CraftHumanEntity who) {
        this.container1.onClose(who);
        this.container2.onClose(who);
        transaction.remove(who);
    }

    @Override
    public List<HumanEntity> getViewers() {
        return transaction;
    }

    @Override
    public org.bukkit.inventory.InventoryHolder getOwner() {
        return null; // This method won't be called since CraftInventoryDoubleChest doesn't defer to here
    }

    @Override
    public void setMaxStackSize(int size) {
        this.container1.setMaxStackSize(size);
        this.container2.setMaxStackSize(size);
    }

    @Override
    public Location getLocation() {
        return container1.getLocation(); // TODO: right?
    }
    // CraftBukkit end

    /**
     * @author wdog5734
     * @reason Bukkit
     */
    @Overwrite
    public int getMaxStackSize() {
        return Math.min(this.container1.getMaxStackSize(), this.container2.getMaxStackSize()); // CraftBukkit - check both sides
    }
}
