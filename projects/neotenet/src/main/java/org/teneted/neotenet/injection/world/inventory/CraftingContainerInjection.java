package org.teneted.neotenet.injection.world.inventory;

import net.minecraft.world.item.crafting.RecipeHolder;

public interface CraftingContainerInjection {

    // CraftBukkit start
    default RecipeHolder<?> getCurrentRecipe() {
        throw new IllegalArgumentException("Not implemented");
    }

    default void setCurrentRecipe(RecipeHolder<?> recipe) {
        throw new IllegalArgumentException("Not implemented");
    }
    // CraftBukkit end
}
