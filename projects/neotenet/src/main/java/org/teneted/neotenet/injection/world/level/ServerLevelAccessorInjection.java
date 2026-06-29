package org.teneted.neotenet.injection.world.level;

import net.minecraft.world.entity.Entity;

public interface ServerLevelAccessorInjection {

    default void addFreshEntityWithPassengers(Entity entity, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason reason) {
        throw new IllegalArgumentException("Not implemented");
    }
}
