package org.teneted.neotenet.mixin.world.level.chunk.status;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.chunk.status.ChunkStatusTasks;
import net.minecraft.world.level.levelgen.WorldOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.stream.Stream;

@Mixin(ChunkStatusTasks.class)
public class MixinChunkStatusTasks {

    @Redirect(method = "generateStructureStarts", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/WorldOptions;generateStructures()Z"))
    private static boolean neotenet$useWorldData(WorldOptions instance, @Local ServerLevel serverlevel) {
        return serverlevel.K.worldGenOptions().generateStructures();
    }

    @Redirect(method = "postLoadProtoChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addWorldGenChunkEntities(Ljava/util/stream/Stream;)V"))
    private static void neotenet$bukkit$addWorldGenChunkEntities(ServerLevel instance, Stream<Entity> p_143328_, @Local(argsOnly = true) List<CompoundTag> p_347609_) {
        // CraftBukkit start - these are spawned serialized (DefinedStructure) and we don't call an add event below at the moment due to ordering complexities
        instance.addWorldGenChunkEntities(EntityType.loadEntitiesRecursive(p_347609_, instance).filter((entity) -> {
            boolean needsRemoval = false;
            net.minecraft.server.dedicated.DedicatedServer server = instance.getCraftServer().getServer();
            if (!server.areNpcsEnabled() && entity instanceof net.minecraft.world.entity.npc.Villager) {
                entity.discard(null); // CraftBukkit - add Bukkit remove cause
                needsRemoval = true;
            }
            if (!server.isSpawningAnimals() && (entity instanceof net.minecraft.world.entity.animal.Animal || entity instanceof net.minecraft.world.entity.animal.WaterAnimal)) {
                entity.discard(null); // CraftBukkit - add Bukkit remove cause
                needsRemoval = true;
            }
            return !needsRemoval;
        }));
        // CraftBukkit end
    }
}
