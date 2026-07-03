package org.teneted.neotenet.injection.world.item.trading;

import org.bukkit.craftbukkit.inventory.CraftMerchantRecipe;

public interface MerchantOfferInjection {

    default CraftMerchantRecipe asBukkit() {
        throw new IllegalArgumentException("Not implemented");
    }
}
