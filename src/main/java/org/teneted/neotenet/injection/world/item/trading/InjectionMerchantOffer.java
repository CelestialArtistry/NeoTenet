package org.teneted.neotenet.injection.world.item.trading;

import org.bukkit.craftbukkit.inventory.CraftMerchantRecipe;

public interface InjectionMerchantOffer {

    default CraftMerchantRecipe asBukkit() {
        throw new IllegalStateException("Not implemented");
    }

}
