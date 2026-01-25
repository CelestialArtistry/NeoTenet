package org.teneted.neotenet.mixin.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import org.bukkit.Location;
import org.bukkit.TreeType;
import org.bukkit.craftbukkit.block.CapturedBlockState;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.event.world.StructureGrowEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SaplingBlock.class)
public class MixinSaplingBlock {

    @Shadow
    @Final
    protected TreeGrower treeGrower;

    @Redirect(method = "advanceTree", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/grower/TreeGrower;growTree(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/chunk/ChunkGenerator;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/util/RandomSource;)Z"))
    private boolean neotenet$growTreeHandle(TreeGrower instance, ServerLevel serverLevel, ChunkGenerator chunkGenerator, BlockPos pos, BlockState blockState, RandomSource randomSource) {
        // CraftBukkit start
        if (serverLevel.captureTreeGeneration) {
            this.treeGrower.growTree(serverLevel, serverLevel.getChunkSource().getGenerator(), pos, blockState, randomSource);
        } else {
            serverLevel.captureTreeGeneration = true;
            this.treeGrower.growTree(serverLevel, serverLevel.getChunkSource().getGenerator(), pos, blockState, randomSource);
            serverLevel.captureTreeGeneration = false;
            if (serverLevel.capturedBlockStates.size() > 0) {
                TreeType treeType = SaplingBlock.treeType;
                SaplingBlock.treeType = null;
                Location location = CraftLocation.toBukkit(pos, serverLevel.getWorld());
                java.util.List<org.bukkit.block.BlockState> blocks = new java.util.ArrayList<>(serverLevel.capturedBlockStates.values());
                serverLevel.capturedBlockStates.clear();
                StructureGrowEvent event = null;
                if (treeType != null) {
                    event = new StructureGrowEvent(location, treeType, false, null, blocks);
                    org.bukkit.Bukkit.getPluginManager().callEvent(event);
                }
                if (event == null || !event.isCancelled()) {
                    for (org.bukkit.block.BlockState blockstate : blocks) {
                        CapturedBlockState.setBlockState(blockstate);
                    }
                }
            }
        }
        // CraftBukkit end
        return true;
    }
}
