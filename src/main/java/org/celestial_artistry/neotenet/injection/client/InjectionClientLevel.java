package org.celestial_artistry.neotenet.injection.client;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.LevelStem;

public interface InjectionClientLevel {

    default ResourceKey<LevelStem> getTypeKey(){
        throw new RuntimeException("Not Implemented");
    }
}
