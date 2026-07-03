package org.teneted.neotenet.injection.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface ContainerOpenersCounterInjection {

    default void onAPIOpen(Level level, BlockPos blockpos, BlockState blockstate) {
        throw new IllegalArgumentException("Not implemented");
    }

    default void onAPIClose(Level level, BlockPos blockpos, BlockState blockstate) {
        throw new IllegalArgumentException("Not implemented");
    }

    default void openerAPICountChanged(Level level, BlockPos blockpos, BlockState blockstate, int i, int j) {
        throw new IllegalArgumentException("Not implemented");
    }
}
