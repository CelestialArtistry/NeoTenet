package org.celestial_artistry.neotenet.injection.core.component;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;

public interface InjectionDataComponentPatch_Builder {

    default void copy(DataComponentPatch orig) {
        throw new IllegalStateException("Not implemented");
    }

    default void clear(DataComponentType<?> type) {
        throw new IllegalStateException("Not implemented");
    }

    default boolean isEmpty() {
        throw new IllegalStateException("Not implemented");
    }

    default boolean isSet(DataComponentType<?> type) {
        throw new IllegalStateException("Not implemented");
    }
}
