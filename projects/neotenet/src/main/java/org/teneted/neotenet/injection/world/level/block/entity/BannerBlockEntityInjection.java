package org.teneted.neotenet.injection.world.level.block.entity;

import net.minecraft.world.level.block.entity.BannerPatternLayers;

public interface BannerBlockEntityInjection {

    default void setPatterns(BannerPatternLayers bannerpatternlayers) {
        throw new IllegalArgumentException("Not implemented");
    }
}
