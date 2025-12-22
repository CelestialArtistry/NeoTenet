package org.celestial_artistry.neotenet.mixin.world.item;

import java.util.List;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.item.EndCrystalItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(EndCrystalItem.class)
public class MixinEndCrystalItem {

    @Inject(method = "useOn", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/boss/enderdragon/EndCrystal;setShowBottom(Z)V",
            shift = At.Shift.AFTER), cancellable = true)
    private void neotenet$handlePlaceEvent(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir,
                                            @Local EndCrystal endCrystal) {
        // CraftBukkit start
        if (CraftEventFactory.callEntityPlaceEvent(context, endCrystal).isCancelled()) {
            cir.setReturnValue(InteractionResult.FAIL);
        }
        // CraftBukkit end
    }

}
