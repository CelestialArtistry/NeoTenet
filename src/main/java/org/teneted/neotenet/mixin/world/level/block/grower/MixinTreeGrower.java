package org.teneted.neotenet.mixin.world.level.block.grower;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.bukkit.TreeType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TreeGrower.class)
public class MixinTreeGrower {

    @Inject(method = "growTree", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/event/level/BlockGrowFeatureEvent;isCanceled()Z", ordinal = 0))
    private void neotenet$setTreeType(ServerLevel p_304396_, ChunkGenerator p_304672_, BlockPos p_304643_, BlockState p_304439_, RandomSource p_304893_, CallbackInfoReturnable<Boolean> cir, @Local(ordinal = 0) Holder<ConfiguredFeature<?, ?>> holder) {
        if (holder != null) {
            setTreeType(holder);
        }
    }

    @Inject(method = "growTree", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/event/EventHooks;fireBlockGrowFeature(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/util/RandomSource;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Holder;)Lnet/neoforged/neoforge/event/level/BlockGrowFeatureEvent;"))
    private void neotenet$setTreeType0(ServerLevel p_304396_, ChunkGenerator p_304672_, BlockPos p_304643_, BlockState p_304439_, RandomSource p_304893_, CallbackInfoReturnable<Boolean> cir, @Local(ordinal = 0) Holder<ConfiguredFeature<?, ?>> holder1) {
        if (holder1 != null) {
            setTreeType(holder1);
        }
    }

    // CraftBukkit start
    private void setTreeType(Holder<ConfiguredFeature<?, ?>> holder) {
        ResourceKey<ConfiguredFeature<?, ?>> worldgentreeabstract = holder.unwrapKey().get();
        if (worldgentreeabstract == TreeFeatures.OAK || worldgentreeabstract == TreeFeatures.OAK_BEES_005) {
            SaplingBlock.treeType = TreeType.TREE;
        } else if (worldgentreeabstract == TreeFeatures.HUGE_RED_MUSHROOM) {
            SaplingBlock.treeType = TreeType.RED_MUSHROOM;
        } else if (worldgentreeabstract == TreeFeatures.HUGE_BROWN_MUSHROOM) {
            SaplingBlock.treeType = TreeType.BROWN_MUSHROOM;
        } else if (worldgentreeabstract == TreeFeatures.JUNGLE_TREE) {
            SaplingBlock.treeType = TreeType.COCOA_TREE;
        } else if (worldgentreeabstract == TreeFeatures.JUNGLE_TREE_NO_VINE) {
            SaplingBlock.treeType = TreeType.SMALL_JUNGLE;
        } else if (worldgentreeabstract == TreeFeatures.PINE) {
            SaplingBlock.treeType = TreeType.TALL_REDWOOD;
        } else if (worldgentreeabstract == TreeFeatures.SPRUCE) {
            SaplingBlock.treeType = TreeType.REDWOOD;
        } else if (worldgentreeabstract == TreeFeatures.ACACIA) {
            SaplingBlock.treeType = TreeType.ACACIA;
        } else if (worldgentreeabstract == TreeFeatures.BIRCH || worldgentreeabstract == TreeFeatures.BIRCH_BEES_005) {
            SaplingBlock.treeType = TreeType.BIRCH;
        } else if (worldgentreeabstract == TreeFeatures.SUPER_BIRCH_BEES_0002) {
            SaplingBlock.treeType = TreeType.TALL_BIRCH;
        } else if (worldgentreeabstract == TreeFeatures.SWAMP_OAK) {
            SaplingBlock.treeType = TreeType.SWAMP;
        } else if (worldgentreeabstract == TreeFeatures.FANCY_OAK || worldgentreeabstract == TreeFeatures.FANCY_OAK_BEES_005) {
            SaplingBlock.treeType = TreeType.BIG_TREE;
        } else if (worldgentreeabstract == TreeFeatures.JUNGLE_BUSH) {
            SaplingBlock.treeType = TreeType.JUNGLE_BUSH;
        } else if (worldgentreeabstract == TreeFeatures.DARK_OAK) {
            SaplingBlock.treeType = TreeType.DARK_OAK;
        } else if (worldgentreeabstract == TreeFeatures.MEGA_SPRUCE) {
            SaplingBlock.treeType = TreeType.MEGA_REDWOOD;
        } else if (worldgentreeabstract == TreeFeatures.MEGA_PINE) {
            SaplingBlock.treeType = TreeType.MEGA_PINE;
        } else if (worldgentreeabstract == TreeFeatures.MEGA_JUNGLE_TREE) {
            SaplingBlock.treeType = TreeType.JUNGLE;
        } else if (worldgentreeabstract == TreeFeatures.AZALEA_TREE) {
            SaplingBlock.treeType = TreeType.AZALEA;
        } else if (worldgentreeabstract == TreeFeatures.MANGROVE) {
            SaplingBlock.treeType = TreeType.MANGROVE;
        } else if (worldgentreeabstract == TreeFeatures.TALL_MANGROVE) {
            SaplingBlock.treeType = TreeType.TALL_MANGROVE;
        } else if (worldgentreeabstract == TreeFeatures.CHERRY || worldgentreeabstract == TreeFeatures.CHERRY_BEES_005) {
            SaplingBlock.treeType = TreeType.CHERRY;
        } else {
            SaplingBlock.treeType = TreeType.MOD; // NeoTenet: Default to MOD
        }
    }
    // CraftBukkit end
}
