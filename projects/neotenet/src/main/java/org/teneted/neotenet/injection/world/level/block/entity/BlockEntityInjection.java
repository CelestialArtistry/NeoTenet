package org.teneted.neotenet.injection.world.level.block.entity;

import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;

import java.util.Set;

public interface BlockEntityInjection {

    default Set<DataComponentType<?>> applyComponentsSet(DataComponentMap prototype, DataComponentPatch patch) {
        throw new IllegalArgumentException("Not implemented");
    }
}
