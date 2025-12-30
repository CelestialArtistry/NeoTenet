package org.teneted.neotenet.mixin.world.level.block;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.SculkBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SculkBlock.class)
public class MixinSculkBlock {

    @Redirect(method = "attemptUseCharge", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/LevelAccessor;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private boolean neotenet$cancelSetBlock(LevelAccessor instance, BlockPos blockPos, BlockState blockState, int i) {
        return false;
    }

    @WrapWithCondition(method = "attemptUseCharge", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/LevelAccessor;playSound(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/core/BlockPos;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"))
    private boolean neotenet$handleBlockSpreadEvent(LevelAccessor instance, Player player, BlockPos blockPos, SoundEvent soundEvent, SoundSource soundSource, float x, float v, @Local(ordinal = 2) BlockPos blockpos1, @Local BlockState blockstate) {
        return CraftEventFactory.handleBlockSpreadEvent(instance, blockPos, blockpos1, blockstate, 3);
    }
}
