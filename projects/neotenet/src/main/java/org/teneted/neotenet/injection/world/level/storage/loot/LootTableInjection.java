package org.teneted.neotenet.injection.world.level.storage.loot;

import net.minecraft.world.Container;
import net.minecraft.world.level.storage.loot.LootParams;

public interface LootTableInjection {

    default void fillInventory(Container container, LootParams params, long optionalRandomSeed, boolean plugin) {
        throw new IllegalArgumentException("Not implemented");
    }
}
