package org.teneted.neotenet.mixin.world.level.levelgen.structure.templatesystem;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.bukkit.craftbukkit.block.CraftBlockEntityState;
import org.bukkit.craftbukkit.block.CraftBlockState;
import org.bukkit.craftbukkit.block.CraftBlockStates;
import org.bukkit.craftbukkit.block.CraftLootable;
import org.bukkit.craftbukkit.persistence.CraftPersistentDataContainer;
import org.bukkit.craftbukkit.util.CraftStructureTransformer;
import org.bukkit.craftbukkit.util.TransformerGeneratorAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StructureTemplate.class)
public abstract class MixinStructureTemplate {

    /*
    @Shadow
    public CraftPersistentDataContainer persistentDataContainer;

    @Inject(method = "save", at = @At("RETURN"))
    private void neotenet$savePdc(CompoundTag tag, CallbackInfoReturnable<CompoundTag> cir) {
        if (!this.persistentDataContainer.isEmpty()) {
            tag.put("BukkitValues", this.persistentDataContainer.toTagCompound());
        }
    }

    @Inject(method = "load", at = @At("RETURN"))
    private void neotenet$loadPdc(HolderGetter<Block> reg, CompoundTag tag, CallbackInfo ci) {
        var base = tag.get("BukkitValues");
        if (base instanceof CompoundTag compoundTag) {
            this.persistentDataContainer.putAll(compoundTag);
        }
    }

    @Redirect(method = "placeInWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructurePlaceSettings;getRandomPalette(Ljava/util/List;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate$Palette;"))
    private void neotenet$unwrap(ServerLevelAccessor serverLevelAccessor, BlockPos blockPos, BlockPos blockPos2, StructurePlaceSettings structurePlaceSettings, RandomSource randomSource, int i,
                                 @Local(allocate = "wrappedAccess") ServerLevelAccessor wrappedAccess,
                                 @Local(allocate = "structureTransformer") CraftStructureTransformer structureTransformer) throws Throwable {
        wrappedAccess = serverLevelAccessor;
        structureTransformer = null;
        if (wrappedAccess instanceof TransformerGeneratorAccess transformerAccess) {
            serverLevelAccessor = transformerAccess.getHandle();
            structureTransformer = transformerAccess.getStructureTransformer();
            // The structureTransformer is not needed if we can not transform blocks therefore we can save a little bit of performance doing this
            if (structureTransformer != null && !structureTransformer.canTransformBlocks()) {
                structureTransformer = null;
            }
        }
        DecorationOps.blackhole().invoke(serverLevelAccessor, wrappedAccess, structureTransformer);
    }

    @Redirect(method = "placeInWorld", at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/world/level/ServerLevelAccessor;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private boolean neotenet$useTransformer(ServerLevelAccessor instance, BlockPos pos, BlockState blockState, int i,
                                            @Local(ordinal = -1) RandomSource randomSource,
                                            @Local(ordinal = -1) StructureTemplate.StructureBlockInfo structureBlockInfo,
                                            @Local(ordinal = -1) BlockState blockstate,
                                            @Local(allocate = "structureTransformer") CraftStructureTransformer structureTransformer) throws Throwable {
        if (structureTransformer != null) {
            var craftBlockState = (CraftBlockState) CraftBlockStates.getBlockState(instance, pos, blockstate, null);
            if (structureBlockInfo.nbt() != null && craftBlockState instanceof CraftBlockEntityState<?> entityState) {
                entityState.loadData(structureBlockInfo.nbt());
                if (craftBlockState instanceof CraftLootable<?> craftLootable) {
                    craftLootable.setSeed(randomSource.nextLong());
                }
            }
            craftBlockState = structureTransformer.transformCraftState(craftBlockState);
            blockstate = craftBlockState.getHandle();
            // Input argument blockState is exactly blockstate
            blockState = blockstate;
            structureBlockInfo = new StructureTemplate.StructureBlockInfo(pos, blockState, (craftBlockState instanceof CraftBlockEntityState<?> craftBlockEntityState ? craftBlockEntityState.getSnapshotNBT() : null));
        }
        DecorationOps.blackhole().invoke(structureBlockInfo, blockstate);
        return (boolean) DecorationOps.callsite().invoke(instance, pos, blockState, i);
    }

    @Redirect(method = "placeInWorld", inject = true, at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructurePlaceSettings;isIgnoreEntities()Z"))
    private void neotenet$resetWrap(ServerLevelAccessor serverLevelAccessor, BlockPos blockPos, BlockPos blockPos2, StructurePlaceSettings structurePlaceSettings, RandomSource randomSource, int i,
                                    @Local(allocate = "wrappedAccess") ServerLevelAccessor wrappedAccess) throws Throwable {
        serverLevelAccessor = wrappedAccess;
        DecorationOps.blackhole().invoke(serverLevelAccessor);
    }*/
}
