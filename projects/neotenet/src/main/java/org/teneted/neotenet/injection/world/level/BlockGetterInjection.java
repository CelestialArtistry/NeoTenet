package org.teneted.neotenet.injection.world.level;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;

public interface BlockGetterInjection {

    default BlockHitResult clip(ClipContext clipcontext1, BlockPos blockpos) {
        throw new IllegalArgumentException("Not implemented");
    }
}