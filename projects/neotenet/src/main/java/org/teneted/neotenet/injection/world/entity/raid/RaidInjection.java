package org.teneted.neotenet.injection.world.entity.raid;

import net.minecraft.world.entity.raid.Raider;

public interface RaidInjection {

    default boolean isInProgress() {
        throw new IllegalArgumentException("Not implemented");
    }

    default java.util.Collection<Raider> getRaiders() {
        throw new IllegalArgumentException("Not implemented");
    }
}
