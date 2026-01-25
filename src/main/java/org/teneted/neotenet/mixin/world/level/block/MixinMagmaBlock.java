package org.teneted.neotenet.mixin.world.level.block;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.MagmaBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(MagmaBlock.class)
public class MixinMagmaBlock {

    @ModifyArg(method = "stepOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"), index = 0)
    private DamageSource neotenet$useDirect(DamageSource p_19946_, @Local(argsOnly = true) Level p_153777_, @Local(argsOnly = true) BlockPos p_153778_) {
        return p_153777_.damageSources().hotFloor().directBlock(p_153777_, p_153778_);
    }
}
