package org.taiyitistmc.mixin.world.level;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraft.world.level.storage.WritableLevelData;
import net.neoforged.neoforge.common.extensions.ILevelExtension;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.block.CapturedBlockState;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.util.CraftSpawnCategory;
import org.bukkit.entity.SpawnCategory;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.generator.ChunkGenerator;
import org.spigotmc.SpigotWorldConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.taiyitistmc.injection.world.level.InjectionLevel;

import javax.annotation.Nullable;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

@Mixin(Level.class)
public abstract class MixinLevel implements LevelAccessor, AutoCloseable, ILevelExtension, InjectionLevel {

    @Shadow
    public SpigotWorldConfig spigotConfig;

    @Shadow
    private CraftWorld world;

    @Shadow
    @Final
    public Object2LongOpenHashMap<SpawnCategory> ticksPerSpawnCategory;

    @Shadow
    public abstract WorldBorder getWorldBorder();

    @Shadow
    public boolean captureTreeGeneration;

    @Shadow
    public Map<BlockPos, CapturedBlockState> capturedBlockStates;

    @Shadow
    public boolean captureBlockStates;

    @Shadow
    public abstract void setBlocksDirty(BlockPos p_46678_, BlockState p_46679_, BlockState p_46680_);

    @Shadow
    @Final
    public boolean isClientSide;

    @Shadow
    public abstract void sendBlockUpdated(BlockPos p_46612_, BlockState p_46613_, BlockState p_46614_, int p_46615_);

    @Shadow
    public abstract void updateNeighbourForOutputSignal(BlockPos p_46718_, Block p_46719_);

    @Shadow
    public boolean preventPoiUpdated;

    @Shadow
    public abstract void onBlockStateChange(BlockPos p_46609_, BlockState p_46610_, BlockState p_46611_);

    @Shadow
    public abstract LevelChunk getChunkAt(BlockPos p_46746_);

    @Shadow
    @Final
    public Thread thread;

    @Shadow
    public Map<BlockPos, BlockEntity> capturedTileEntities;

    @Shadow
    public static ChunkGenerator generator;

    @Shadow
    @Final
    private ResourceKey<Level> dimension;

    @Inject(method = "<init>(Lnet/minecraft/world/level/storage/WritableLevelData;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/core/RegistryAccess;Lnet/minecraft/core/Holder;Ljava/util/function/Supplier;ZZJI)V", at = @At("RETURN"), order = 1001)
    private void taiyitist$init(WritableLevelData writableLevelData, ResourceKey resourceKey, RegistryAccess registryAccess, Holder holder, Supplier supplier, boolean p_270904_, boolean p_270470_, long p_270248_, int p_270466_, CallbackInfo ci) {
        if ((Object) this instanceof ServerLevel serverLevel) {
            this.spigotConfig = new SpigotWorldConfig((MinecraftServer.getServer().storageSource.getDimensionPath(resourceKey).getFileName().toFile().getName()));
            getWorldBorder().world = serverLevel;
        }
        // CraftBukkit Ticks things
        for (SpawnCategory spawnCategory : SpawnCategory.values()) {
            if (CraftSpawnCategory.isValidForLimits(spawnCategory)) {
                this.ticksPerSpawnCategory.put(spawnCategory, (long) this.getCraftServer().getTicksPerSpawns(spawnCategory));
            }
        }
        // CraftBukkit end
    }

    @Override
    public CraftWorld getWorld() {
        return this.world;
    }

    @Override
    public CraftServer getCraftServer() {
        return (CraftServer) Bukkit.getServer();
    }

    @Inject(method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isOutsideBuildHeight(Lnet/minecraft/core/BlockPos;)Z"), cancellable = true)
    private void taiyitist$captureTree(BlockPos p_46605_, BlockState p_46606_, int p_46607_, int p_46608_, CallbackInfoReturnable<Boolean> cir) {
        // CraftBukkit start - tree generation
        if (this.captureTreeGeneration) {
            CapturedBlockState blockstate = capturedBlockStates.get(p_46605_);
            if (blockstate == null) {
                blockstate = CapturedBlockState.getTreeBlockState(((Level) (Object) this), p_46605_, p_46607_);
                this.capturedBlockStates.put(p_46605_.immutable(), blockstate);
            }
            blockstate.setData(p_46606_);
            blockstate.setFlag(p_46607_);
            cir.setReturnValue(true);
        }
        // CraftBukkit end
    }

    @Redirect(method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/LevelChunk;setBlockState(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Z)Lnet/minecraft/world/level/block/state/BlockState;"))
    private BlockState taiyitist$captureBlockStates(LevelChunk instance, BlockPos blockentity, BlockState block, boolean flag1, @Local(argsOnly = true) BlockPos p_46605_, @Local(ordinal = 0, argsOnly = true) int p_46607_, @Local(ordinal = 0) boolean captured) {
        // CraftBukkit start - capture blockstates
        if (this.captureBlockStates && !this.capturedBlockStates.containsKey(p_46605_)) {
            CapturedBlockState blockstate = CapturedBlockState.getBlockState(((Level) (Object) this), p_46605_, p_46607_);
            this.capturedBlockStates.put(p_46605_.immutable(), blockstate);
             captured = true;
        }
        return instance.setBlockState(p_46605_, block, (p_46607_ & 64) != 0, (p_46607_ & 1024) == 0); // CraftBukkit custom NO_PLACE flag
        // CraftBukkit end
    }

    @WrapWithCondition(method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;markAndNotifyBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/chunk/LevelChunk;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;II)V"))
    private boolean taiyitist$addBukkitCheck(Level instance, BlockPos blockPos, LevelChunk p_46605_, BlockState levelchunk, BlockState blockstate, int p_46606_, int p_46607_) {
        return !this.captureBlockStates;
    }

    // CraftBukkit start - Split off from above in order to directly send client and physic updates
    @Override
    public void notifyAndUpdatePhysics(BlockPos blockposition, LevelChunk chunk, BlockState oldBlock, BlockState newBlock, BlockState actualBlock, int i, int j) {
        BlockState iblockdata = newBlock;
        BlockState iblockdata1 = oldBlock;
        BlockState iblockdata2 = actualBlock;
        if (iblockdata2 == iblockdata) {
            if (iblockdata1 != iblockdata2) {
                this.setBlocksDirty(blockposition, iblockdata1, iblockdata2);
            }

            if ((i & 2) != 0 && (!this.isClientSide || (i & 4) == 0) && (this.isClientSide || chunk == null || (chunk.getFullStatus() != null && chunk.getFullStatus().isOrAfter(FullChunkStatus.BLOCK_TICKING)))) { // allow chunk to be null here as chunk.isReady() is false when we send our notification during block placement
                this.sendBlockUpdated(blockposition, iblockdata1, iblockdata, i);
            }

            if ((i & 1) != 0) {
                this.blockUpdated(blockposition, iblockdata1.getBlock());
                if (!this.isClientSide && iblockdata.hasAnalogOutputSignal()) {
                    this.updateNeighbourForOutputSignal(blockposition, newBlock.getBlock());
                }
            }

            if ((i & 16) == 0 && j > 0) {
                int k = i & -34;

                // CraftBukkit start
                iblockdata1.updateIndirectNeighbourShapes(this, blockposition, k, j - 1); // Don't call an event for the old block to limit event spam
                CraftWorld world = ((ServerLevel) (Object) this).getWorld();
                if (world != null) {
                    BlockPhysicsEvent event = new BlockPhysicsEvent(world.getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ()), CraftBlockData.fromData(iblockdata));
                    this.getCraftServer().getPluginManager().callEvent(event);

                    if (event.isCancelled()) {
                        return;
                    }
                }
                // CraftBukkit end
                iblockdata.updateNeighbourShapes(this, blockposition, k, j - 1);
                iblockdata.updateIndirectNeighbourShapes(this, blockposition, k, j - 1);
            }

            // CraftBukkit start - SPIGOT-5710
            if (!preventPoiUpdated) {
                this.onBlockStateChange(blockposition, iblockdata1, iblockdata2);
            }
            // CraftBukkit end
        }
    }
    // CraftBukkit end

    @Inject(method = "getBlockState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isOutsideBuildHeight(Lnet/minecraft/core/BlockPos;)Z"), cancellable = true)
    private void taiyitist$checkCaptures(BlockPos p_46732_, CallbackInfoReturnable<BlockState> cir) {
        // CraftBukkit start - tree generation
        if (captureTreeGeneration) {
            CapturedBlockState previous = capturedBlockStates.get(p_46732_);
            if (previous != null) {
                cir.setReturnValue(previous.getHandle());
            }
        }
        // CraftBukkit end
    }

    private AtomicBoolean taiyitist$validate = new AtomicBoolean(true);

    @Inject(method = "getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;", at = @At("HEAD"))
    private void taiyitist$markValidate(BlockPos p_46716_, CallbackInfoReturnable<BlockEntity> cir) {
        taiyitist$validate.set(true);
    }
    @Inject(method = "getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isOutsideBuildHeight(Lnet/minecraft/core/BlockPos;)Z"), cancellable = true)
    private void taiyitist$getBlockEntity(BlockPos p_46716_, CallbackInfoReturnable<BlockEntity> cir) {
        if (capturedTileEntities.containsKey(p_46716_)) {
            cir.setReturnValue(capturedTileEntities.get(p_46716_));
        }
        // CraftBukkit end
    }

    @Nullable
    @Override
    public BlockEntity getBlockEntity(BlockPos p_46716_, boolean validate) {
        if (capturedTileEntities.containsKey(p_46716_)) {
            return capturedTileEntities.get(p_46716_);
        }
        // CraftBukkit end
        if (this.isOutsideBuildHeight(p_46716_)) {
            return null;
        } else {
            return !this.isClientSide && Thread.currentThread() != this.thread
                    ? null
                    : this.getChunkAt(p_46716_).getBlockEntity(p_46716_, LevelChunk.EntityCreationType.IMMEDIATE);
        }
    }

    @Inject(method = "setBlockEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getChunkAt(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/chunk/LevelChunk;"), cancellable = true)
    private void taiyitist$addCheckCap(BlockEntity p_151524_, CallbackInfo ci, @Local BlockPos blockpos) {
        // CraftBukkit start
        if (captureBlockStates) {
            capturedTileEntities.put(blockpos.immutable(), p_151524_);
            ci.cancel();
            return;
        }
        // CraftBukkit end
    }

    @Override
    public ResourceKey<LevelStem> getTypeKey() {
        return Registries.levelToLevelStem(dimension);
    }
}