package org.taiyitistmc.mixin.world.item.crafting;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmokingRecipe;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.inventory.CraftRecipe;
import org.bukkit.craftbukkit.inventory.CraftSmokingRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.taiyitistmc.bukkit.inventory.recipe.BannerModdedRecipe;

@Mixin(SmokingRecipe.class)
public abstract class MixinSmokingRecipe extends AbstractCookingRecipe {


    public MixinSmokingRecipe(RecipeType<?> recipeType, String string, CookingBookCategory cookingBookCategory, Ingredient ingredient, ItemStack itemStack, float f, int i) {
        super(recipeType, string, cookingBookCategory, ingredient, itemStack, f, i);
    }

    @Override
    public org.bukkit.inventory.Recipe toBukkitRecipe(NamespacedKey id) {
        if (this.result.isEmpty()) {
            return new BannerModdedRecipe(id, (SmokingRecipe) (Object) this);
        }
        CraftItemStack result = CraftItemStack.asCraftMirror(this.result);
        CraftSmokingRecipe recipe = new CraftSmokingRecipe(id, result, CraftRecipe.toBukkit(this.ingredient), this.experience, this.cookingTime);
        recipe.setGroup(this.group);
        recipe.setCategory(CraftRecipe.getCategory(this.category()));

        return recipe;
    }
}
