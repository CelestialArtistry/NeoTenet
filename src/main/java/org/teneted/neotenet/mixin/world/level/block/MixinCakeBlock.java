package org.teneted.neotenet.mixin.world.level.block;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.level.block.CakeBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CakeBlock.class)
public class MixinCakeBlock {

    @Redirect(method = "eat",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/food/FoodData;eat(IF)V"))
    private static void neotenet$eatEvent(FoodData instance, int p_38708_, float p_38709_, @Local(argsOnly = true) Player p_51189_) {
        // CraftBukkit start
        // entityhuman.getFoodData().eat(2, 0.1F);
        int oldFoodLevel = p_51189_.getFoodData().foodLevel;

        org.bukkit.event.entity.FoodLevelChangeEvent event = org.bukkit.craftbukkit.event.CraftEventFactory.callFoodLevelChangeEvent(p_51189_, 2 + oldFoodLevel);

        if (!event.isCancelled()) {
            p_51189_.getFoodData().eat(event.getFoodLevel() - oldFoodLevel, 0.1F);
        }

        ((net.minecraft.server.level.ServerPlayer) p_51189_).getBukkitEntity().sendHealthUpdate();
        // CraftBukkit end
    }
}
