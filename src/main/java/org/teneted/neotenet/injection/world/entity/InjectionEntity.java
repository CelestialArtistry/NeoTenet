package org.teneted.neotenet.injection.world.entity;

import java.util.Set;
import java.util.UUID;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.event.CraftPortalEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.Nullable;

public interface InjectionEntity {

    default void setOrigin(@javax.annotation.Nonnull Location location) {
        throw new IllegalStateException("Not implemented");
    }

    default void refreshEntityData(ServerPlayer to) {
        throw new IllegalStateException("Not implemented");
    }

    @Nullable
    default org.bukkit.util.Vector getOriginVector() {
        throw new IllegalStateException("Not implemented");
    }

    @Nullable
    default UUID getOriginWorld() {
        throw new IllegalStateException("Not implemented");
    }

    default  boolean teleportTo(ServerLevel worldserver, double d0, double d1, double d2, Set<RelativeMovement> set, float f, float f1, PlayerTeleportEvent.TeleportCause cause) {
        throw new IllegalStateException("Not implemented");
    }

    default CraftEntity getBukkitEntity() {
        throw new IllegalStateException("Not implemented");
    }

    default int getDefaultMaxAirSupply() {
        throw new IllegalStateException("Not implemented");
    }

    default float getBukkitYaw() {
        throw new IllegalStateException("Not implemented");
    }

    default boolean isChunkLoaded() {
        throw new IllegalStateException("Not implemented");
    }

    default void postTick() {
        throw new IllegalStateException("Not implemented");
    }

    default SoundEvent getSwimSound0() {
        throw new IllegalStateException("Not implemented");
    }

    default SoundEvent getSwimSplashSound0() {
        throw new IllegalStateException("Not implemented");
    }

    default SoundEvent getSwimHighSpeedSplashSound0() {
        throw new IllegalStateException("Not implemented");
    }

    default boolean canCollideWithBukkit(Entity entity) {
        throw new IllegalStateException("Not implemented");
    }

    default Entity teleportTo(ServerLevel worldserver, Vec3 location) {
        throw new IllegalStateException("Not implemented");
    }

    default CraftPortalEvent callPortalEvent(Entity entity, ServerLevel exitWorldServer, Vec3 exitPosition, PlayerTeleportEvent.TeleportCause cause, int searchRadius, int creationRadius) {
        throw new IllegalStateException("Not implemented");
    }

    default void discard(EntityRemoveEvent.Cause cause) {
        throw new IllegalStateException("Not implemented");
    }

    default void remove(Entity.RemovalReason entity_removalreason, EntityRemoveEvent.Cause cause) {
        throw new IllegalStateException("Not implemented");
    }

    default void pushRemoveCause(EntityRemoveEvent.Cause cause) {
        throw new IllegalStateException("Not implemented");
    }

    default void pushSpawnCause(CreatureSpawnEvent.SpawnReason reason) {
        throw new IllegalStateException("Not implemented");
    }

    default void igniteForSeconds(float i, boolean callEvent) {
        throw new IllegalStateException("Not implemented");
    }
    default boolean saveAsPassenger(CompoundTag p_20087_,boolean includeAll)  {
        throw new IllegalStateException("Not implemented");
    }
}
