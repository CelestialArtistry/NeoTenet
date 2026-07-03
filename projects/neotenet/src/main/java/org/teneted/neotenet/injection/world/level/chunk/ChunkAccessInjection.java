package org.teneted.neotenet.injection.world.level.chunk;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;

public interface ChunkAccessInjection {

    default void setBiome(int i, int j, int k, Holder<Biome> biome) {
        throw new IllegalArgumentException("Not implemented");
    }
}
