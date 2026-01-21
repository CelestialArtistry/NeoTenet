package org.teneted.neotenet.mixin.world.level.block;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VineBlock.class)
public class MixinVineBlock {

    @Inject(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/VineBlock;isAcceptableNeighbour(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Z"))
    private void neotenet$markSource(BlockState p_222655_, ServerLevel p_222656_, BlockPos p_222657_, RandomSource p_222658_, CallbackInfo ci, @Share("source") LocalRef<BlockPos> source) {
        // CraftBukkit start - Call BlockSpreadEvent
        source.set(p_222657_);
    }

    @Redirect(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", ordinal = 0))
    private boolean neotenet$handleBlockSpreadEvent0(ServerLevel instance, BlockPos blockPos, BlockState blockState, int i, @Share("source") LocalRef<BlockPos> source) {
        return CraftEventFactory.handleBlockSpreadEvent(instance, source.get(), blockPos, blockState, i);
    }

    @Redirect(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", ordinal = 1))
    private boolean neotenet$handleBlockSpreadEvent1(ServerLevel instance, BlockPos blockPos, BlockState blockState, int i, @Share("source") LocalRef<BlockPos> source) {
        return CraftEventFactory.handleBlockSpreadEvent(instance, source.get(), blockPos, blockState, i);
    }

    @Redirect(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", ordinal = 2))
    private boolean neotenet$handleBlockSpreadEvent2(ServerLevel instance, BlockPos blockPos, BlockState blockState, int i, @Share("source") LocalRef<BlockPos> source) {
        return CraftEventFactory.handleBlockSpreadEvent(instance, source.get(), blockPos, blockState, i);
    }

    @Redirect(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", ordinal = 3))
    private boolean neotenet$handleBlockSpreadEvent3(ServerLevel instance, BlockPos blockPos, BlockState blockState, int i, @Share("source") LocalRef<BlockPos> source) {
        return CraftEventFactory.handleBlockSpreadEvent(instance, source.get(), blockPos, blockState, i);
    }

    @Redirect(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", ordinal = 4))
    private boolean neotenet$handleBlockSpreadEvent4(ServerLevel instance, BlockPos blockPos, BlockState blockState, int i, @Share("source") LocalRef<BlockPos> source) {
        return CraftEventFactory.handleBlockSpreadEvent(instance, source.get(), blockPos, blockState, i);
    }

    @Redirect(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", ordinal = 7))
    private boolean neotenet$handleBlockSpreadEvent5(ServerLevel instance, BlockPos blockPos, BlockState blockState, int i, @Local(ordinal = 0, argsOnly = true) BlockPos pos) {
        return CraftEventFactory.handleBlockSpreadEvent(instance, pos, blockPos, blockState, i);
    }

    @Redirect(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", ordinal = 8))
    private boolean neotenet$handleBlockSpreadEvent6(ServerLevel instance, BlockPos blockPos, BlockState blockState, int i, @Local(ordinal = 0, argsOnly = true) BlockPos pos) {
        return CraftEventFactory.handleBlockSpreadEvent(instance, pos, blockPos, blockState, i);
    }

    @Redirect(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", ordinal = 5))
    private boolean neotenet$handleBlockGrowEvent0(ServerLevel instance, BlockPos blockPos, BlockState blockState, int i) {
        return CraftEventFactory.handleBlockGrowEvent(instance, blockPos, blockState, i);
    }

    @Redirect(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", ordinal = 6))
    private boolean neotenet$handleBlockGrowEvent1(ServerLevel instance, BlockPos blockPos, BlockState blockState, int i) {
        return CraftEventFactory.handleBlockGrowEvent(instance, blockPos, blockState, i);
    }

}
