package org.teneted.neotenet.mixin.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LecternBlock.class)
public class MixinLecternBlock {

    @Redirect(method = "popBook", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;"))
    private BlockEntity neotenet$pluginSolved(Level instance, BlockPos pos) {
        return instance.getBlockEntity(pos, false);
    }

    @Inject(method = "popBook", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/Direction;getStepX()I"), cancellable = true)
    private void neotenet$checkValid(BlockState p_54588_, Level p_54589_, BlockPos p_54590_, CallbackInfo ci) {
        if (p_54588_.isEmpty()) {
            ci.cancel();
            return;
        } // CraftBukkit - SPIGOT-5500
    }
}
