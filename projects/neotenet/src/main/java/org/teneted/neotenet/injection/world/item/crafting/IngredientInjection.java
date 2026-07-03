package org.teneted.neotenet.injection.world.item.crafting;

import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface IngredientInjection {

    default boolean isExact() {
        throw new IllegalArgumentException("Not implemented");
    }

    default List<ItemStack> itemStacks() {
        throw new IllegalArgumentException("Not implemented");
    }
}
