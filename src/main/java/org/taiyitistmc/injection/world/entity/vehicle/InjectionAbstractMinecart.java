package org.taiyitistmc.injection.world.entity.vehicle;

import org.bukkit.util.Vector;

public interface InjectionAbstractMinecart {

    default Vector getFlyingVelocityMod() {
        throw new IllegalStateException("Not implemented");
    }

    default void setFlyingVelocityMod(Vector flying) {
        throw new IllegalStateException("Not implemented");
    }

    default Vector getDerailedVelocityMod() {
        throw new IllegalStateException("Not implemented");
    }

    default void setDerailedVelocityMod(Vector derailed) {
        throw new IllegalStateException("Not implemented");
    }
}
