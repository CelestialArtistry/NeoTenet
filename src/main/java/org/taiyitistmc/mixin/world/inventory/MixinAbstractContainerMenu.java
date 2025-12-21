package org.taiyitistmc.mixin.world.inventory;

import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerSynchronizer;
import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.inventory.InventoryView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(AbstractContainerMenu.class)
public abstract class MixinAbstractContainerMenu {

    @Shadow
    private ItemStack remoteCarried;

    @Shadow
    @Nullable
    private ContainerSynchronizer synchronizer;

    @Shadow
    public abstract ItemStack getCarried();

    @Unique
    private Component title;

    @Unique
    private InventoryView bukkitView;


    // This provides a fallback that returns null
    public InventoryView getBukkitView() {
        return this.bukkitView;
    }

    public void setBukkitView(InventoryView view) {
        this.bukkitView = view;
    }

    public void transferTo(AbstractContainerMenu other, CraftHumanEntity player) {
        InventoryView source = this.getBukkitView();
        InventoryView destination = other.getBukkitView();
        if (source != null) {
            ((CraftInventory) source.getTopInventory()).getInventory().onClose(player);
            ((CraftInventory) source.getBottomInventory()).getInventory().onClose(player);
        }
        if (destination != null) {
            ((CraftInventory) destination.getTopInventory()).getInventory().onOpen(player);
            ((CraftInventory) destination.getBottomInventory()).getInventory().onOpen(player);
        }
    }

    public Component getTitle() {
        if (this.title == null) {
            return Component.empty();
        }
        return this.title;
    }

    public void setTitle(Component title) {
        this.title = title;
    }

    public void broadcastCarriedItem() {
        ItemStack carried = this.getCarried();
        this.remoteCarried = carried.copy();
        if (this.synchronizer != null) {
            this.synchronizer.sendCarriedChange((AbstractContainerMenu) (Object) this, carried.copy());
        }
    }
}
