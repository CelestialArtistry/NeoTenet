package org.teneted.neotenet.mixin.world.level.block.piston;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.AbstractList;
import java.util.List;

@Mixin(PistonBaseBlock.class)
public class MixinPistonBaseBlock {

    @Shadow
    @Final
    private boolean isSticky;

    @Inject(method = "checkIfExtend", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;blockEvent(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;II)V", ordinal = 1), cancellable = true)
    private void neotenet$callBlockPistonRetractEvent(Level p_60168_, BlockPos p_60169_, BlockState p_60170_, CallbackInfo ci, @Local Direction direction){
        // CraftBukkit start
        if (!this.isSticky) {
            org.bukkit.block.Block block = p_60168_.getWorld().getBlockAt(p_60169_.getX(), p_60169_.getY(), p_60169_.getZ());
            BlockPistonRetractEvent event = new BlockPistonRetractEvent(block, ImmutableList.<org.bukkit.block.Block>of(), CraftBlock.notchToBlockFace(direction));
            p_60168_.getCraftServer().getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                ci.cancel();
                return;
            }
        }
        // PAIL: checkME - what happened to setTypeAndData?
        // CraftBukkit end
    }

    @Inject(method = "moveBlocks", at = @At(value = "INVOKE", target = "Ljava/util/List;size()I", ordinal = 2), cancellable = true)
    private void neotenet$callBlockPistonExtendEvent(Level p_60182_, BlockPos p_60183_, Direction p_60184_, boolean p_60185_, CallbackInfoReturnable<Boolean> cir, @Local PistonStructureResolver pistonstructureresolver, @Local(ordinal = 1) Direction direction1) {
        // CraftBukkit start
        final org.bukkit.block.Block bblock = p_60182_.getWorld().getBlockAt(p_60183_.getX(), p_60183_.getY(), p_60183_.getZ());

        final List<BlockPos> moved = pistonstructureresolver.getToPush();
        final List<BlockPos> broken = pistonstructureresolver.getToDestroy();

        List<org.bukkit.block.Block> blocks = new AbstractList<Block>() {

            @Override
            public int size() {
                return moved.size() + broken.size();
            }

            @Override
            public org.bukkit.block.Block get(int index) {
                if (index >= size() || index < 0) {
                    throw new ArrayIndexOutOfBoundsException(index);
                }
                BlockPos pos = (BlockPos) (index < moved.size() ? moved.get(index) : broken.get(index - moved.size()));
                return bblock.getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ());
            }
        };
        org.bukkit.event.block.BlockPistonEvent event;
        if (p_60185_) {
            event = new BlockPistonExtendEvent(bblock, blocks, CraftBlock.notchToBlockFace(direction1));
        } else {
            event = new BlockPistonRetractEvent(bblock, blocks, CraftBlock.notchToBlockFace(direction1));
        }
        p_60182_.getCraftServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            for (BlockPos b : broken) {
                p_60182_.sendBlockUpdated(b, Blocks.AIR.defaultBlockState(), p_60182_.getBlockState(b), 3);
            }
            for (BlockPos b : moved) {
                p_60182_.sendBlockUpdated(b, Blocks.AIR.defaultBlockState(), p_60182_.getBlockState(b), 3);
                b = b.relative(direction1);
                p_60182_.sendBlockUpdated(b, Blocks.AIR.defaultBlockState(), p_60182_.getBlockState(b), 3);
            }
            cir.setReturnValue(false);
        }
        // CraftBukkit end
    }
}
