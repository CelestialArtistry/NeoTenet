package org.teneted.neotenet.mixin.world.item;

import com.llamalad7.mixinextras.sugar.Cancellable;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnderpearlItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderpearlItem.class)
public class MixinEnderpearlItem extends Item {

    public MixinEnderpearlItem(Properties properties) {
        super(properties);
    }

    @Redirect(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean neotenet$handleAdding(Level instance, Entity entity, @Local(argsOnly = true) Player p_41191_, @Cancellable CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir, @Local(ordinal = 0) ItemStack itemstack) {
        // CraftBukkit start - change order
        if (!instance.addFreshEntity(entity)) {
            if (p_41191_ instanceof ServerPlayer) {
                ((ServerPlayer) p_41191_).getBukkitEntity().updateInventory();
            }
            cir.setReturnValue(InteractionResultHolder.fail(itemstack));
        }
        // CraftBukkit end
        return true;
    }
}
