package org.teneted.neotenet.injection.world.level.storage;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.validation.ContentValidationException;

import java.io.IOException;

public interface LevelStorageSourceInjection {

    default LevelStorageSource.LevelStorageAccess validateAndCreateAccess(String levelId, ResourceKey<LevelStem> dimensionType) throws IOException, ContentValidationException { // CraftBukkit
        throw new IllegalArgumentException("Not implemented");
    }

    default LevelStorageSource.LevelStorageAccess createAccess(String levelId, ResourceKey<LevelStem> dimensionType) throws IOException { // CraftBukkit
        throw new IllegalArgumentException("Not implemented");
    }
}
