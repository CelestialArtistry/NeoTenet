package org.teneted.neotenet.injection.world.inventory;

import org.bukkit.craftbukkit.inventory.view.CraftMerchantView;
import org.bukkit.inventory.InventoryView;

public interface MerchantMenuInjection extends AbstractContainerMenuInjection{

    @Override
    default CraftMerchantView getBukkitView() {
        throw new IllegalArgumentException("Not implemented");
    }
}
