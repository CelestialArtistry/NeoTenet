package org.teneted.neotenet.injection.world.item;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.Item;

public interface ItemStackInjection {

    default void restorePatch(DataComponentPatch datacomponentpatch) {
        throw new IllegalArgumentException("Not implemented");
    }

    @Deprecated
    default void setItem(Holder<Item> item) {
        throw new IllegalArgumentException("Not implemented");
    }
}
