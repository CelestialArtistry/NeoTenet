package org.teneted.neotenet.injection.world.entity;

public interface InjectionItemBasedSteering {

    default void setBoostTicks(int ticks) {
        throw new IllegalStateException("Not implemented");
    }
}
