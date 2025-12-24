package org.teneted.neotenet.injection.world.item.crafting;

public interface InjectionIngredient {

    default boolean isVanilla() {
        throw new IllegalStateException("Not implemented");
    }
}
