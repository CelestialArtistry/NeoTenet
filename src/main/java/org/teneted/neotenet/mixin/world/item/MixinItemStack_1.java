package org.teneted.neotenet.mixin.world.item;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.teneted.neotenet.injection.world.item.InjectionItemStack;

@Mixin(targets = "net.minecraft.world.item.ItemStack$1")
public class MixinItemStack_1 {

    @Redirect(method = "decode(Lnet/minecraft/network/RegistryFriendlyByteBuf;)Lnet/minecraft/world/item/ItemStack;",
            at = @At(value = "NEW", target = "(Lnet/minecraft/core/Holder;ILnet/minecraft/core/component/DataComponentPatch;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack neotenet$setItemMeta(Holder p_312081_, int p_41605_, DataComponentPatch p_330362_) {
        // CraftBukkit start
        ItemStack itemstack = new ItemStack(p_312081_, p_41605_, p_330362_);
        if (!p_330362_.isEmpty()) {
            CraftItemStack.setItemMeta(itemstack, CraftItemStack.getItemMeta(itemstack));
        }
        return itemstack;
        // CraftBukkit end
    }

    @ModifyExpressionValue(method = "encode(Lnet/minecraft/network/RegistryFriendlyByteBuf;Lnet/minecraft/world/item/ItemStack;)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z"))
    private boolean neotenet$checkValid(boolean original, @Local(argsOnly = true) ItemStack p_320873_) {
        return original || p_320873_.getItem() == null;
    }
}
