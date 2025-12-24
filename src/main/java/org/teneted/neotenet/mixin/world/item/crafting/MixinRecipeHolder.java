package org.teneted.neotenet.mixin.world.item.crafting;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.inventory.Recipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.teneted.neotenet.injection.world.item.crafting.InjectionRecipeHolder;

@Mixin(RecipeHolder.class)
public class MixinRecipeHolder<T extends net.minecraft.world.item.crafting.Recipe<?>> implements InjectionRecipeHolder {

    @Shadow
    @Final
    private T value;
    @Shadow
    @Final
    private ResourceLocation id;

    @Override
    public Recipe toBukkitRecipe() {
        return this.value.toBukkitRecipe(CraftNamespacedKey.fromMinecraft(this.id));
    }
}
