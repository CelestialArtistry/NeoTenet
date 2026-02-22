package org.teneted.neotenet.mixin.world.level.levelgen.structure.stuctures;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.structures.DesertPyramidStructure;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DesertPyramidStructure.class)
public class MixinDesertPyramidStructure {

    @Inject(method = "placeSuspiciousSand", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/WorldGenLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private static void neotenet$setLoot(BoundingBox p_279472_, WorldGenLevel p_279193_, BlockPos p_279136_, CallbackInfo ci) {
        // CraftBukkit start
        if (p_279193_ instanceof org.bukkit.craftbukkit.util.TransformerGeneratorAccess transformerAccess) {
            org.bukkit.craftbukkit.block.CraftBrushableBlock brushableState = (org.bukkit.craftbukkit.block.CraftBrushableBlock) org.bukkit.craftbukkit.block.CraftBlockStates.getBlockState(p_279193_, p_279136_, Blocks.SUSPICIOUS_SAND.defaultBlockState(), null);
            brushableState.setLootTable(org.bukkit.craftbukkit.CraftLootTable.minecraftToBukkit(BuiltInLootTables.DESERT_PYRAMID_ARCHAEOLOGY));
            brushableState.setSeed(p_279136_.asLong());
            transformerAccess.setCraftBlock(p_279136_, brushableState, 2);
            return;
        }
        // CraftBukkit end
    }
}
