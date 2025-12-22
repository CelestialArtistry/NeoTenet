package org.celestial_artistry.neotenet.injection.world.entity;

public interface InjectionItemBasedSteering {

    default void setBoostTicks(int ticks) {
        throw new IllegalStateException("Not implemented");
    }
}
