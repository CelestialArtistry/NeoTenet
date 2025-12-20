package org.taiyitistmc.mixin.world.item.crafting;

import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import org.bukkit.NamespacedKey;
import org.spongepowered.asm.mixin.Mixin;
import org.taiyitistmc.injection.world.item.crafting.InjectionRecipe;

@Mixin(Recipe.class)
public interface MixinRecipe<C extends Container> extends InjectionRecipe {

    @Override
    default org.bukkit.inventory.Recipe toBukkitRecipe(NamespacedKey id) {
        return null;
    }
}
