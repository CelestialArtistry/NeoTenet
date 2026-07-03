package org.teneted.neotenet.injection.world.entity.vehicle.minecart;

import org.bukkit.util.Vector;

public interface AbstractMinecartInjection {

    default Vector getFlyingVelocityMod() {
        throw new IllegalArgumentException("Not implemented");
    }

    default void setFlyingVelocityMod(Vector flying) {
        throw new IllegalArgumentException("Not implemented");
    }

    default Vector getDerailedVelocityMod() {
        throw new IllegalArgumentException("Not implemented");
    }

    default void setDerailedVelocityMod(Vector derailed) {
        throw new IllegalArgumentException("Not implemented");
    }
}
