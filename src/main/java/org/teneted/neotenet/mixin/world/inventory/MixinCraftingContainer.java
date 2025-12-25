package org.teneted.neotenet.mixin.world.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CraftingContainer.class)
public interface MixinCraftingContainer extends Container, StackedContentsCompatible {

    // CraftBukkit start
    @Override
    default RecipeHolder<?> getCurrentRecipe() {
        return null;
    }

    @Override
    default void setCurrentRecipe(RecipeHolder<?> recipe) {
    }
    // CraftBukkit end
}
