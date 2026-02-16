package org.teneted.neotenet.mixin.world.entity.projectile;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Fireball.class)
public class MixinFireball {

    @WrapWithCondition(method = "readAdditionalSaveData", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/Fireball;setItem(Lnet/minecraft/world/item/ItemStack;)V"))
    private boolean neotenet$checkStack(Fireball instance, ItemStack itemStack) {
        return !itemStack.isEmpty();
    }
}
