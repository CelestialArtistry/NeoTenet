package org.taiyitistmc.injection.world.food;

import net.minecraft.world.food.FoodData;
import org.bukkit.inventory.ItemStack;

public interface InjectionFoodProperties {

    default void eat(ItemStack itemstack, FoodData foodinfo) {
        throw new RuntimeException("Not implemented");
    }
}
