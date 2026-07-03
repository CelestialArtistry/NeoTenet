package org.teneted.neotenet.injection.world.level.block.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public interface BeehiveBlockEntityInjection {

    default List<Entity> releaseBees(BlockState blockstate, BeehiveBlockEntity.BeeReleaseStatus beehiveblockentity_beereleasestatus, boolean force) {
        throw new IllegalArgumentException("Not implemented");
    }
}
