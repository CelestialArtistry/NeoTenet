package org.teneted.neotenet.injection.world.food;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;

public interface InjectionFoodData {

    default Player getEntityhuman() {
        throw new IllegalStateException("Not implemented");
    }

    default void setEntityhuman(Player entityhuman) {
        throw new IllegalStateException("Not implemented");
    }

    default void eat(ItemStack itemstack, FoodProperties foodinfo) {
        throw new IllegalStateException("Not implemented");
    }
}
