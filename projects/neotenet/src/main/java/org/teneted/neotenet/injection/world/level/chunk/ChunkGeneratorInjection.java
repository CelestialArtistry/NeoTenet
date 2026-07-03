package org.teneted.neotenet.injection.world.level.chunk;

import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkAccess;

public interface ChunkGeneratorInjection {

    default void addVanillaDecorations(WorldGenLevel level, ChunkAccess chunk, StructureManager structureManager) {
        throw new IllegalArgumentException("Not implemented");
    }

    default void applyBiomeDecoration(WorldGenLevel level, ChunkAccess chunk, StructureManager structureManager) {
        throw new IllegalArgumentException("Not implemented");
    }

    default void applyBiomeDecoration(WorldGenLevel worldgenlevel, ChunkAccess chunkaccess, StructureManager structuremanager, boolean vanilla) {
        throw new IllegalArgumentException("Not implemented");
    }
}
