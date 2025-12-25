package org.teneted.neotenet.mixin.world.item;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.FireChargeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.block.BlockIgniteEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.teneted.neotenet.bukkit.DistValidate;

@Mixin(FireChargeItem.class)
public class MixinFireChargeItem {

    @Inject(method = "useOn", cancellable = true,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/FireChargeItem;playSound(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V"))
    public void neotenet$blockIgnite(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir, @Local(ordinal = 0) Level world, @Local(ordinal = 0) BlockPos blockPos) {
        if (DistValidate.isValid(context) && CraftEventFactory.callBlockIgniteEvent(world, blockPos, BlockIgniteEvent.IgniteCause.FIREBALL, context.getPlayer()).isCancelled()) {
            if (!context.getPlayer().getAbilities().instabuild) {
                context.getItemInHand().shrink(1);
            }
            cir.setReturnValue(InteractionResult.PASS);
        }
    }
}
