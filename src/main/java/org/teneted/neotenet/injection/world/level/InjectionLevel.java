package org.teneted.neotenet.injection.world.level;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.LevelStem;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;

public interface InjectionLevel {

    default ResourceKey<LevelStem> getTypeKey(){
        throw new IllegalStateException("Not implemented");
    }

    default CraftWorld getWorld() {
        throw new IllegalStateException("Not implemented");
    }

    default CraftServer getCraftServer() {
        throw new IllegalStateException("Not implemented");
    }

    default void notifyAndUpdatePhysics(BlockPos blockposition, LevelChunk chunk, BlockState oldBlock, BlockState newBlock, BlockState actualBlock, int i, int j) {
        throw new IllegalStateException("Not implemented");
    }

    default BlockEntity getBlockEntity(BlockPos blockposition, boolean validate) {
        throw new IllegalStateException("Not implemented");
    }
}
