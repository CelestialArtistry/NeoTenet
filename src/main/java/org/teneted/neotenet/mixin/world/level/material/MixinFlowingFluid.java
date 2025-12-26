package org.teneted.neotenet.mixin.world.level.material;

import com.llamalad7.mixinextras.sugar.Cancellable;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.FluidLevelChangeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FlowingFluid.class)
public class MixinFlowingFluid {

    @Inject(method = "spread", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FlowingFluid;spreadTo(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/world/level/material/FluidState;)V"), cancellable = true)
    private void neotenet$callBlockFromToEvent(Level p_255851_, BlockPos p_76012_, FluidState p_76013_, CallbackInfo ci) {
        // CraftBukkit start
        org.bukkit.block.Block source = CraftBlock.at(p_255851_, p_76012_);
        BlockFromToEvent event = new BlockFromToEvent(source, BlockFace.DOWN);
        p_255851_.getCraftServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            ci.cancel();
            return;
        }
        // CraftBukkit end
    }

    @Inject(method = "spreadToSides", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FlowingFluid;spreadTo(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/world/level/material/FluidState;)V"), cancellable = true)
    private void neotenet$callBlockFromToEvent0(Level p_256644_, BlockPos p_76016_, FluidState p_76017_, BlockState p_76018_, CallbackInfo ci, @Local(ordinal = 0) Direction direction) {
        // CraftBukkit start
        org.bukkit.block.Block source = CraftBlock.at(p_256644_, p_76016_);
        BlockFromToEvent event = new BlockFromToEvent(source, org.bukkit.craftbukkit.block.CraftBlock.notchToBlockFace(direction));
        p_256644_.getCraftServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            ci.cancel();
            return;
        }
        // CraftBukkit end
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", ordinal = 0))
    private boolean neotenet$callFluidLevelChangeEvent0(Level instance, BlockPos p_46601_, BlockState p_46602_, int p_46603_, @Cancellable CallbackInfo ci) {
        // CraftBukkit start
        FluidLevelChangeEvent event = CraftEventFactory.callFluidLevelChangeEvent(instance, p_46601_, Blocks.AIR.defaultBlockState());
        if (event.isCancelled()) {
            ci.cancel();
        }
        instance.setBlock(p_46601_, ((CraftBlockData) event.getNewData()).getState(), 3);
        // CraftBukkit end
        return true;
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", ordinal = 1))
    private boolean neotenet$callFluidLevelChangeEvent1(Level instance, BlockPos p_46601_, BlockState p_46602_, int p_46603_, @Cancellable CallbackInfo ci) {
        // CraftBukkit start
        FluidLevelChangeEvent event = CraftEventFactory.callFluidLevelChangeEvent(instance, p_46601_, Blocks.AIR.defaultBlockState());
        if (event.isCancelled()) {
            ci.cancel();
        }
        instance.setBlock(p_46601_, ((CraftBlockData) event.getNewData()).getState(), 2);
        // CraftBukkit end
        return true;
    }
}
