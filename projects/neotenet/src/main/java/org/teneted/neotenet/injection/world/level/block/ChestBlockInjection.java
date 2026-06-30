package org.teneted.neotenet.injection.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public interface ChestBlockInjection {

    default @Nullable MenuProvider getMenuProvider(BlockState blockstate, Level level, BlockPos blockpos, boolean ignoreObstructions) {
        throw new IllegalArgumentException("Not implemented");
    }
}
