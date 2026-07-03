package org.teneted.neotenet.injection.world.level.border;

public interface WorldBorderInjection {

    default void applyInitialSettings(long gameTime, boolean force) {
        throw new IllegalArgumentException("Not implemented");
    }
}
