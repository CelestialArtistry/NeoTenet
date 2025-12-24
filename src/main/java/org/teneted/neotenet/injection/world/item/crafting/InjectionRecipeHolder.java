package org.teneted.neotenet.injection.world.item.crafting;

public interface InjectionRecipeHolder {

    default org.bukkit.inventory.Recipe toBukkitRecipe() {
        throw new IllegalStateException("Not implemented");
    }
}
