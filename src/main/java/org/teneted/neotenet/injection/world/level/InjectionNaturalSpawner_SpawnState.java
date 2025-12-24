package org.teneted.neotenet.injection.world.level;

import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;

public interface InjectionNaturalSpawner_SpawnState {

    default  boolean canSpawnForCategory(MobCategory enumcreaturetype, ChunkPos chunkcoordintpair, int limit) {
        throw new IllegalStateException("Not implemented");
    }
}
