package org.teneted.neotenet.injection.world.entity;

public interface ItemBasedSteeringInjection {

    default void setBoostTicks(int ticks) {
        throw new IllegalArgumentException("Not implemented");
    }
}
