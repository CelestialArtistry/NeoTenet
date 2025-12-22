package org.celestial_artistry.neotenet.injection.world.level.block;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

public interface InjectionChestBlockDoubleInventory {

    default AbstractContainerMenu createMenu(int i, Inventory playerinventory, Player entityhuman) {
        throw new IllegalStateException("Not implemented");
    }

    default Component getDisplayName() {
        throw new IllegalStateException("Not implemented");
    }
}
