package org.celestial_artistry.neotenet.injection.world.level.block.entity;

import java.util.Set;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import org.bukkit.inventory.InventoryHolder;
import org.celestial_artistry.neotenet.injection.world.InjectionContainer;

public interface InjectionBlockEntity extends InjectionContainer {

    default InventoryHolder getOwner() {
        throw new IllegalStateException("Not implemented");
    }

    default void setPatterns(BannerPatternLayers bannerPatternLayers) {
        throw new IllegalStateException("Not implemented");
    }

    default Set<DataComponentType<?>> applyComponentsSet(DataComponentMap datacomponentmap, DataComponentPatch datacomponentpatch) {
        return Set.of();
    }
}
