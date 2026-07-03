package org.teneted.neotenet.injection.world.entity.projectile;

import net.minecraft.world.entity.Entity;
import org.bukkit.event.entity.EntityRemoveEvent;

public interface ShulkerBulletInjection {

    default Entity getTarget() {
        throw new IllegalArgumentException("Not implemented");
    }

    default void setTarget(Entity e) {
        throw new IllegalArgumentException("Not implemented");
    }

    default void destroy(EntityRemoveEvent.Cause cause) {
        throw new IllegalArgumentException("Not implemented");
    }
}
