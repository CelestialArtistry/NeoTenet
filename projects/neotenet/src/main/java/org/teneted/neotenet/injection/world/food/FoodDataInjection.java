package org.teneted.neotenet.injection.world.food;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;

public interface FoodDataInjection {

    default void eat(FoodProperties foodproperties, ItemStack itemstack, ServerPlayer serverplayer) {
        throw new IllegalArgumentException("Not implemented");
    }
}
