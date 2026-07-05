package org.teneted.neotenet.mixin.core.stats;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.ServerRecipeBook;
import net.minecraft.world.item.crafting.Recipe;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerRecipeBook.class)
public class ServerRecipeBookMixin {

    @ModifyExpressionValue(method = "addRecipes", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/Recipe;isSpecial()Z"))
    private boolean neotenet$handlePlayerRecipeListUpdateEvent(boolean original, @Local(argsOnly = true) ServerPlayer player, @Local ResourceKey<Recipe<?>> id) {
        return original && CraftEventFactory.handlePlayerRecipeListUpdateEvent(player, id.identifier());
    }

    @ModifyExpressionValue(method = "addRecipes",
            at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z"))
    private boolean neotenet$checkIfAdd(boolean original, @Local(argsOnly = true) ServerPlayer player) {
        return original && player.connection != null;
    }

    @ModifyExpressionValue(method = "removeRecipes",
            at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z"))
    private boolean neotenet$checkIfRemove(boolean original, @Local(argsOnly = true) ServerPlayer player) {
        return original && player.connection != null;
    }
}
