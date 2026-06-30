package org.teneted.neotenet.injection.core.component;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;

public interface DataComponentPatch_BuilderInjection {

    default void copy(DataComponentPatch orig) {
        throw new IllegalArgumentException("Not implemented");
    }

    default void clear(DataComponentType<?> type) {
        throw new IllegalArgumentException("Not implemented");
    }

    default boolean isSet(DataComponentType<?> type) {
        throw new IllegalArgumentException("Not implemented");
    }

    default boolean isEmpty() {
        throw new IllegalArgumentException("Not implemented");
    }
}