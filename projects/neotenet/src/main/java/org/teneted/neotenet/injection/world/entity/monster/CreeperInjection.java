package org.teneted.neotenet.injection.world.entity.monster;

public interface CreeperInjection {

    default void setPowered(boolean powered) {
        throw new IllegalArgumentException("Not implemented");
    }
}
