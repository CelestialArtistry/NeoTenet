package org.teneted.neotenet.mixin.world.level.block;

import com.llamalad7.mixinextras.sugar.Cancellable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StemBlock.class)
public class MixinStemBlock {

    @Redirect(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", ordinal = 0))
    private boolean neotenet$handleBlockGrowEvent(ServerLevel instance, BlockPos pos, BlockState blockState, int i) {
        return CraftEventFactory.handleBlockGrowEvent(instance, pos, blockState, 2); // CraftBukkit
    }

    @Redirect(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z", ordinal = 0))
    private boolean neotenet$handleBlockGrowEvent0(ServerLevel instance, BlockPos pos, BlockState blockState, @Cancellable CallbackInfo ci) {
        // CraftBukkit start
        if (!CraftEventFactory.handleBlockGrowEvent(instance, pos, blockState)) {
            ci.cancel();
        }
        // CraftBukkit end
        return true;
    }

    @Redirect(method = "performBonemeal", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private boolean neotenet$handleBlockGrowEvent1(ServerLevel instance, BlockPos pos, BlockState blockState, int i) {
        return CraftEventFactory.handleBlockGrowEvent(instance, pos, blockState, 2); // CraftBukkit
    }
}
