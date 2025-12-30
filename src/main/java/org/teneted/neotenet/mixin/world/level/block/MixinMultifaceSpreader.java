package org.teneted.neotenet.mixin.world.level.block;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.MultifaceSpreader;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MultifaceSpreader.SpreadConfig.class)
public interface MixinMultifaceSpreader {

    @Redirect(method = "placeBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/LevelAccessor;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private boolean neotenet$handleBlockSpreadEvent(LevelAccessor instance, BlockPos blockPos, BlockState blockState, int i, @Local(argsOnly = true) MultifaceSpreader.SpreadPos p_221703_) {
        return CraftEventFactory.handleBlockSpreadEvent(instance, p_221703_.source(), blockPos, blockState, i);
    }
}
