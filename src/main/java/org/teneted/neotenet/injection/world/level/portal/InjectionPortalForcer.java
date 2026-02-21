package org.teneted.neotenet.injection.world.level.portal;

import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.border.WorldBorder;

import java.util.Optional;

public interface InjectionPortalForcer {

    default Optional<BlockPos> findClosestPortalPosition(BlockPos blockPos, boolean flag, WorldBorder worldBorder, int i) {
        throw new RuntimeException("Not implemented");
    }

    default Optional<BlockUtil.FoundRectangle> createPortal(BlockPos blockPos, Direction.Axis axis, net.minecraft.world.entity.Entity entity, int createRadius) {
        throw new RuntimeException("Not implemented");
    }
}
