package org.teneted.neotenet.injection.world.inventory;

import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.bukkit.inventory.InventoryView;

public interface AbstractContainerMenuInjection {

    default InventoryView getBukkitView() {
        throw new IllegalArgumentException("Not implemented");
    }

    default void transferTo(AbstractContainerMenu other, org.bukkit.craftbukkit.entity.CraftHumanEntity player) {
        throw new IllegalArgumentException("Not implemented");
    }

    default Component getTitle() {
        throw new IllegalArgumentException("Not implemented");
    }

    default void setTitle(Component title) {
        throw new IllegalArgumentException("Not implemented");
    }

    default void startOpen() {
        throw new IllegalArgumentException("Not implemented");
    }

    default void broadcastCarriedItem() {
        throw new IllegalArgumentException("Not implemented");
    }
}
