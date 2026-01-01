package org.teneted.neotenet.injection.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;

public interface InjectionBaseFireBlock {

    default void fireExtinguished(net.minecraft.world.level.LevelAccessor world, BlockPos position) {
        if (!org.bukkit.craftbukkit.event.CraftEventFactory.callBlockFadeEvent(world, position, Blocks.AIR.defaultBlockState()).isCancelled()) {
            world.removeBlock(position, false);
        }
    }
}
