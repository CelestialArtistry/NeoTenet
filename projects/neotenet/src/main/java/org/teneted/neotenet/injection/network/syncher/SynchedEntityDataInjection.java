package org.teneted.neotenet.injection.network.syncher;

import net.minecraft.network.syncher.EntityDataAccessor;

public interface SynchedEntityDataInjection {

    default <T> void markDirty(EntityDataAccessor<T> entitydataaccessor) {
        throw new IllegalArgumentException("Not implemented");
    }
}
