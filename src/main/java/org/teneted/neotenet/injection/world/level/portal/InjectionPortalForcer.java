package org.teneted.neotenet.injection.world.level.portal;

import net.minecraft.world.entity.Entity;

public interface InjectionPortalForcer {

    default void pushPortalCreate(Entity entity, int createRadius) {
        throw new RuntimeException("Not implemented");
    }

    default void pushSearchRadius(int searchRadius) {
        throw new RuntimeException("Not implemented");
    }
}
