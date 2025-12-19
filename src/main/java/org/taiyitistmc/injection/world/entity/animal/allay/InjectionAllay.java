package org.taiyitistmc.injection.world.entity.animal.allay;

import net.minecraft.world.entity.animal.allay.Allay;

public interface InjectionAllay {

    default Allay duplicateAllay0() {
        throw new IllegalStateException("Not implemented");
    }

    default void setCanDuplicate(boolean canDuplicate) {
        throw new IllegalStateException("Not implemented");
    }
}
