package org.celestial_artistry.neotenet.injection.world.entity.monster;

public interface InjectionCreeper {

    default void setPowered(boolean power) {
        throw new IllegalStateException("Not implemented");
    }
}
