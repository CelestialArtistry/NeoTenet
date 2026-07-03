package org.teneted.neotenet.injection.world.item.crafting;

public interface RecipeInjection {

    default  org.bukkit.inventory.Recipe toBukkitRecipe(org.bukkit.NamespacedKey id) {
        throw new IllegalArgumentException("Not implemented");
    }
}
