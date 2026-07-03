package org.teneted.neotenet.injection.world.item.crafting;

public interface RecipeHolderInjection {

    default  org.bukkit.inventory.Recipe toBukkitRecipe() {
        throw new IllegalArgumentException("Not implemented");
    }
}
