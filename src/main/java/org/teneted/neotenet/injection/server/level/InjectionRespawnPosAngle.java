package org.teneted.neotenet.injection.server.level;

public interface InjectionRespawnPosAngle {

    default boolean isBedSpawn() {
        throw new IllegalStateException("Not implemented");
    }

    default boolean isAnchorSpawn() {
        throw new IllegalStateException("Not implemented");
    }
}
