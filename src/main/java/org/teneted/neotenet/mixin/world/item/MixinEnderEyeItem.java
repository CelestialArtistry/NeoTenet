package org.teneted.neotenet.mixin.world.item;

import com.llamalad7.mixinextras.sugar.Cancellable;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.EnderEyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderEyeItem.class)
public class MixinEnderEyeItem {

    @Redirect(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean neotenet$handleAddEntity(Level instance, Entity entity, @Cancellable CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir, @Local(ordinal = 0) ItemStack itemstack) {
        // CraftBukkit start
        if (!instance.addFreshEntity(entity)) {
            cir.setReturnValue(new InteractionResultHolder(InteractionResult.FAIL, itemstack));
        }
        // CraftBukkit end
        return true;
    }
}
