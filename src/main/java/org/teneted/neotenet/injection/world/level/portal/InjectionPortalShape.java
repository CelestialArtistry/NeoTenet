package org.teneted.neotenet.injection.world.level.portal;

import net.minecraft.world.entity.Entity;

public interface InjectionPortalShape {

    default boolean createPortalBlocks(Entity entity) {
        throw new IllegalStateException("Not Implemented");
    }
}
