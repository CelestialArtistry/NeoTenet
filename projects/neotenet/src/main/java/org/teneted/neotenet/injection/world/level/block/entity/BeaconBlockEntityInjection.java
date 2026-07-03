package org.teneted.neotenet.injection.world.level.block.entity;

import org.bukkit.potion.PotionEffect;

public interface BeaconBlockEntityInjection {

    default PotionEffect getPrimaryEffect() {
        throw new IllegalArgumentException("Not implemented");
    }

    default PotionEffect getSecondaryEffect() {
        throw new IllegalArgumentException("Not implemented");
    }
}
