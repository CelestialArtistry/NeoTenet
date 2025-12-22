package org.celestial_artistry.neotenet.injection.world.entity.projectile;

public interface InjectionAbstractHurtingProjectile {

    default void setDirection(double d3, double d4, double d5) {
        throw new IllegalStateException("Not implemented");
    }
}
