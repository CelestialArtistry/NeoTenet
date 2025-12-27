package org.teneted.neotenet.mixin.world.level.block;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CauldronBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.bukkit.event.block.CauldronLevelChangeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CauldronBlock.class)
public class MixinCauldronBlock {

    @Redirect(method = "receiveStalactiteDrip", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private boolean neotenet$changeLevel(Level instance, BlockPos p_46598_, BlockState p_46599_, @Local(argsOnly = true) BlockState p_152940_) {
        LayeredCauldronBlock.changeLevel(p_152940_, instance, p_46598_, p_46599_, null, CauldronLevelChangeEvent.ChangeReason.NATURAL_FILL); // CraftBukkit
        return true;
    }

    @Redirect(method = "receiveStalactiteDrip", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;gameEvent(Lnet/minecraft/core/Holder;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/gameevent/GameEvent$Context;)V"))
    private void neotenet$cancelSetBlock(Level instance, Holder holder, BlockPos blockPos, GameEvent.Context context) {
    }
}
