package org.teneted.neotenet.injection.world.entity.animal;

import net.minecraft.world.item.ItemStack;

public interface InjectionAnimal {

    default ItemStack getBreedItem() {
        throw new IllegalStateException("Not implemented");
    }
}
