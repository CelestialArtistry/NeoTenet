package org.teneted.neotenet.injection.world.item.crafting;

import org.bukkit.NamespacedKey;

public interface InjectionShapelessRecipe extends InjectionRecipe{

    @Override
    default org.bukkit.inventory.ShapelessRecipe toBukkitRecipe(NamespacedKey id) {
        throw new IllegalStateException("Not implemented");
    }
}
