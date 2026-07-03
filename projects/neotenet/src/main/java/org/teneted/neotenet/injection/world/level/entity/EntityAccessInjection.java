package org.teneted.neotenet.injection.world.level.entity;

import net.minecraft.world.entity.Entity;
import org.bukkit.event.entity.EntityRemoveEvent;

public interface EntityAccessInjection {

    default void setRemoved(Entity.RemovalReason entity_removalreason, EntityRemoveEvent.Cause cause) {
        throw new IllegalArgumentException("Not implemented");
    }
}
