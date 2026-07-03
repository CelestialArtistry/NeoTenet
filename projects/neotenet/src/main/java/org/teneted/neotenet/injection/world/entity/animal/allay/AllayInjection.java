package org.teneted.neotenet.injection.world.entity.animal.allay;

import net.minecraft.world.entity.animal.allay.Allay;

public interface AllayInjection {

    default void setCanDuplicate(boolean canDuplicate) {
        throw new IllegalArgumentException("Not implemented");
    }

    default Allay duplicateAllay0() {
        throw new IllegalArgumentException("Not implemented");
    }
}
