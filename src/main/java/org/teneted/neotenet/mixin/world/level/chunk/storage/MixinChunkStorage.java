package org.teneted.neotenet.mixin.world.level.chunk.storage;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.mojang.serialization.MapCodec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.chunk.storage.ChunkStorage;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.teneted.neotenet.util.LevelWrapper;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

@Mixin(ChunkStorage.class)
public abstract class MixinChunkStorage {

    @Shadow
    public abstract CompletableFuture<Optional<CompoundTag>> read(ChunkPos p_223455_);

    @Shadow
    public abstract CompoundTag upgradeChunkTag(ResourceKey<Level> p_188289_, Supplier<DimensionDataStorage> p_188290_, CompoundTag p_188291_, Optional<ResourceKey<MapCodec<? extends ChunkGenerator>>> p_188292_);

    private AtomicReference<LevelAccessor> neotenet$generatoraccess = new AtomicReference<>();
    private AtomicReference<ChunkPos> neotenet$pos = new AtomicReference<>();

    @Inject(method = "upgradeChunkTag", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/storage/ChunkStorage;injectDatafixingContext(Lnet/minecraft/nbt/CompoundTag;Lnet/minecraft/resources/ResourceKey;Ljava/util/Optional;)V"))
    private void neotenet$configBelowZeroGenerationInExistingChunks(ResourceKey<Level> p_188289_, Supplier<DimensionDataStorage> p_188290_, CompoundTag p_188291_, Optional<ResourceKey<MapCodec<? extends ChunkGenerator>>> p_188292_, CallbackInfoReturnable<CompoundTag> cir, @Local int i, @Share("stopBelowZeroFlag") LocalBooleanRef stopBelowZeroFlag) {
        // Spigot start - SPIGOT-6806: Quick and dirty way to prevent below zero generation in old chunks, by setting the status to heightmap instead of empty
        boolean stopBelowZero = false;
        stopBelowZeroFlag.set(stopBelowZero);
        boolean belowZeroGenerationInExistingChunks = (neotenet$generatoraccess.get() != null) ? ((ServerLevel) neotenet$generatoraccess.get()).spigotConfig.belowZeroGenerationInExistingChunks : org.spigotmc.SpigotConfig.belowZeroGenerationInExistingChunks;

        if (i <= 2730 && !belowZeroGenerationInExistingChunks) {
            stopBelowZero = "full".equals(p_188291_.getCompound("Level").getString("Status"));
        }
        // Spigot end
    }

    @Inject(method = "upgradeChunkTag", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/storage/ChunkStorage;removeDatafixingContext(Lnet/minecraft/nbt/CompoundTag;)V"))
    private void neotenet$configStopBelowZero(ResourceKey<Level> p_188289_, Supplier<DimensionDataStorage> p_188290_, CompoundTag p_188291_, Optional<ResourceKey<MapCodec<? extends ChunkGenerator>>> p_188292_, CallbackInfoReturnable<CompoundTag> cir, @Share("stopBelowZeroFlag") LocalBooleanRef stopBelowZeroFlag) {
        // Spigot start
        if (stopBelowZeroFlag.get()) {
            p_188291_.putString("Status", net.minecraft.core.registries.BuiltInRegistries.CHUNK_STATUS.getKey(ChunkStatus.SPAWN).toString());
        }
        // Spigot end
    }

    // CraftBukkit start
    private boolean check(ServerChunkCache cps, int x, int z) {
        ChunkPos pos = new ChunkPos(x, z);
        if (cps != null) {
            com.google.common.base.Preconditions.checkState(org.bukkit.Bukkit.isPrimaryThread(), "primary thread");
            if (cps.hasChunk(x, z)) {
                return true;
            }
        }

        CompoundTag nbt;
        try {
            nbt = read(pos).get().orElse(null);
        } catch (InterruptedException | ExecutionException ex) {
            throw new RuntimeException(ex);
        }
        if (nbt != null) {
            CompoundTag level = nbt.getCompound("Level");
            if (level.getBoolean("TerrainPopulated")) {
                return true;
            }

            ChunkStatus status = ChunkStatus.byName(level.getString("Status"));
            if (status != null && status.isOrAfter(ChunkStatus.FEATURES)) {
                return true;
            }
        }

        return false;
    }

    @Definition(id = "i", local = @Local(type = int.class))
    @Expression("i < 1493")
    @Inject(method = "upgradeChunkTag", at = @At("MIXINEXTRAS:EXPRESSION"))
    private void neotenet$putInfo(ResourceKey<Level> p_188289_, Supplier<DimensionDataStorage> p_188290_, CompoundTag p_188291_, Optional<ResourceKey<MapCodec<? extends ChunkGenerator>>> p_188292_, CallbackInfoReturnable<CompoundTag> cir, @Local int i) {
        // CraftBukkit start
        if (i < 1466) {
            CompoundTag level = p_188291_.getCompound("Level");
            if (level.getBoolean("TerrainPopulated") && !level.getBoolean("LightPopulated")) {
                ServerChunkCache cps = (neotenet$generatoraccess.get() == null) ? null : ((ServerLevel) neotenet$generatoraccess.get()).getChunkSource();
                if (check(cps, neotenet$pos.get().x - 1, neotenet$pos.get().z) && check(cps, neotenet$pos.get().x - 1, neotenet$pos.get().z - 1) && check(cps, neotenet$pos.get().x, neotenet$pos.get().z - 1)) {
                    level.putBoolean("LightPopulated", true);
                }
            }
        }
        // CraftBukkit end
    }

    public CompoundTag upgradeChunkTag(
            ResourceKey<LevelStem> p_188289_,
            Supplier<DimensionDataStorage> p_188290_,
            CompoundTag p_188291_,
            Optional<ResourceKey<MapCodec<? extends ChunkGenerator>>> p_188292_,
            ChunkPos pos, @Nullable LevelAccessor generatoraccess
    ) {
        neotenet$generatoraccess.set(generatoraccess);
        neotenet$pos.set(pos);
        return upgradeChunkTag(LevelWrapper.levelStemToLevel(p_188289_), p_188290_, p_188291_, p_188292_);
    }
}
