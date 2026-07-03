package org.teneted.neotenet.injection.world.item.crafting;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

public interface RecipeMapInjection {

    default void addRecipe(RecipeHolder<?> irecipe) {
        throw new IllegalArgumentException("Not implemented");
    }

    default boolean removeRecipe(ResourceKey<Recipe<?>> mcKey) {
        throw new IllegalArgumentException("Not implemented");
    }
}
