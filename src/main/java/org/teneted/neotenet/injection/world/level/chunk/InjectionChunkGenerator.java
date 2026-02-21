package org.teneted.neotenet.injection.world.level.chunk;

import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkAccess;

public interface InjectionChunkGenerator {

    default void applyBiomeDecoration(WorldGenLevel level, ChunkAccess chunkAccess, StructureManager structureFeatureManager, boolean vanilla) {
        throw new IllegalStateException("Not implemented");
    }

    default void addVanillaDecorations(WorldGenLevel p_223087_, ChunkAccess p_223088_, StructureManager p_223089_) {
        throw new IllegalStateException("Not implemented");
    }
}
