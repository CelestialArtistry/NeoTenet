package org.teneted.neotenet.mixin.core.network.syncher;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.teneted.neotenet.injection.network.syncher.SynchedEntityDataInjection;

@Mixin(SynchedEntityData.class)
public abstract class SynchedEntityDataMixin implements SynchedEntityDataInjection {


    @Shadow
    protected abstract <T> SynchedEntityData.DataItem<T> getItem(EntityDataAccessor<T> accessor);

    @Shadow
    private boolean isDirty;

    @Override
    // CraftBukkit start - add method from above
    public <T> void markDirty(EntityDataAccessor<T> entitydataaccessor) {
        this.getItem(entitydataaccessor).setDirty(true);
        this.isDirty = true;
    }
    // CraftBukkit end
}
