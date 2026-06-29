package org.teneted.neotenet.injection.world.level;

import net.minecraft.world.entity.Entity;

public interface LevelWriterInjection {

    default boolean addFreshEntity(Entity entity, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason reason) {
        throw new IllegalArgumentException("Not implemented");
    }
}
