package org.teneted.neotenet.injection.world.level;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.LevelStem;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.jetbrains.annotations.Nullable;

public interface LevelInjection {

    default CraftWorld getWorld() {
        throw new IllegalArgumentException("Not implemented");
    }

    default CraftServer getCraftServer() {
        throw new IllegalArgumentException("Not implemented");
    }

    default ResourceKey<LevelStem> getTypeKey() {
        throw new IllegalArgumentException("Not implemented");
    }

    default void notifyAndUpdatePhysics(BlockPos blockpos, LevelChunk levelchunk, BlockState oldBlock, BlockState newBlock, BlockState actualBlock, int i, int j) {
        throw new IllegalArgumentException("Not implemented");
    }

    default @Nullable BlockEntity getBlockEntity(BlockPos pos, boolean validate) {
        throw new IllegalArgumentException("Not implemented");
    }
}