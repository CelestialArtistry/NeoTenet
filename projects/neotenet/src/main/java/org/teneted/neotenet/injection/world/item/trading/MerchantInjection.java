package org.teneted.neotenet.injection.world.item.trading;

import org.bukkit.craftbukkit.inventory.CraftMerchant;

public interface MerchantInjection {

    default CraftMerchant getCraftMerchant() {
        throw new IllegalArgumentException("Not implemented");
    }
}
