package org.teneted.neotenet.mixin.world.level.entity;

import io.izzel.arclight.mixin.Decorate;
import io.izzel.arclight.mixin.Local;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.storage.EntityStorage;
import net.minecraft.world.level.entity.ChunkEntities;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntityPersistentStorage;
import net.minecraft.world.level.entity.EntitySection;
import net.minecraft.world.level.entity.EntitySectionStorage;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Mixin(PersistentEntitySectionManager.class)
public abstract class MixinPersistentEntitySectionManager<T extends EntityAccess> {

    // @formatter:off
    @Shadow public abstract void close() throws IOException;
    @Shadow @Final private EntityPersistentStorage<T> permanentStorage;
    @Shadow @Final EntitySectionStorage<T> sectionStorage;
    @Shadow @Final private Long2ObjectMap<PersistentEntitySectionManager.ChunkLoadStatus> chunkLoadStatuses;
    // @formatter:on

    public void close(boolean save) throws IOException {
        if (save) {
            this.close();
        } else {
            this.permanentStorage.close();
        }
    }

    public List<Entity> getEntities(ChunkPos chunkCoordIntPair) {
        return sectionStorage.getExistingSectionsInChunk(chunkCoordIntPair.toLong())
            .flatMap(EntitySection::getEntities).map(o -> (Entity) o).collect(Collectors.toList());
    }

    public boolean isPending(long cord) {
        return this.chunkLoadStatuses.get(cord) == PersistentEntitySectionManager.ChunkLoadStatus.PENDING;
    }

    @Unique private boolean neotenet$fireEvent = false;

    @Decorate(method = "storeChunkSections", inject = true,
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/entity/EntityPersistentStorage;storeEntities(Lnet/minecraft/world/level/entity/ChunkEntities;)V"))
    private void neotenet$fireUnload(long pos, @Local(ordinal = -1) List<T> list) {
        if (neotenet$fireEvent) {
            CraftEventFactory.callEntitiesUnloadEvent(((EntityStorage) permanentStorage).level, new ChunkPos(pos),
                list.stream().map(entity -> (Entity) entity).collect(Collectors.toList()));
        }
    }

    @Inject(method = "storeChunkSections", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/entity/EntityPersistentStorage;storeEntities(Lnet/minecraft/world/level/entity/ChunkEntities;)V"))
    private void neotenet$resetFlag(long pos, Consumer<T> consumer, CallbackInfoReturnable<Boolean> cir) {
        neotenet$fireEvent = false;
    }

    @Inject(method = "processChunkUnload", at = @At("HEAD"))
    private void neotenet$fireEvent(long pChunkPosValue, CallbackInfoReturnable<Boolean> cir) {
        neotenet$fireEvent = true;
    }

    @Inject(method = "processPendingLoads", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", shift = At.Shift.AFTER, remap = false, target = "Lit/unimi/dsi/fastutil/longs/Long2ObjectMap;put(JLjava/lang/Object;)Ljava/lang/Object;"))
    private void neotenet$fireLoad(CallbackInfo ci, ChunkEntities<T> chunkEntities) {
        List<Entity> entities = getEntities(chunkEntities.getPos());
        CraftEventFactory.callEntitiesLoadEvent(((EntityStorage) permanentStorage).level, chunkEntities.getPos(), entities);
    }

    @Inject(method = "unloadEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/entity/EntityAccess;setRemoved(Lnet/minecraft/world/entity/Entity$RemovalReason;)V"))
    private void neotenet$unloadCause(EntityAccess entityAccess, CallbackInfo ci) {
        if (entityAccess instanceof Entity entity) {
            entity.pushRemoveCause(EntityRemoveEvent.Cause.UNLOAD);
        }
    }
}
