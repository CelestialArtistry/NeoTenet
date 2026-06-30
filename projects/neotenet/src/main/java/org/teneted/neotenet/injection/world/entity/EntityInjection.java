package org.teneted.neotenet.injection.world.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.event.CraftPortalEvent;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.entity.EntityUnleashEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface EntityInjection {

    static boolean isLevelAtLeast(ValueInput tag, int level) {
        int updateLevel = tag.getIntOr("Bukkit.updateLevel", -1);
        return updateLevel != -1 && tag.getIntOr("Bukkit.updateLevel", -1) >= level;
    }

    default CraftEntity getBukkitEntity() {
        throw new IllegalArgumentException("Not implemented");
    }

    default int getDefaultMaxAirSupply() {
        throw new IllegalArgumentException("Not implemented");
    }

    default float getBukkitYaw() {
        throw new IllegalArgumentException("Not implemented");
    }

    default boolean isChunkLoaded() {
        throw new IllegalArgumentException("Not implemented");
    }

    default void discard(EntityRemoveEvent.Cause cause) {
        throw new IllegalArgumentException("Not implemented");
    }

    default void refreshEntityData(ServerPlayer to) {
        throw new IllegalArgumentException("Not implemented");
    }

    default void remove(Entity.RemovalReason entity_removalreason, EntityRemoveEvent.Cause cause) {
        throw new IllegalArgumentException("Not implemented");
    }

    default void postTick() {
        throw new IllegalArgumentException("Not implemented");
    }

    default void igniteForSeconds(float numberOfSeconds, boolean callEvent) {
        throw new IllegalArgumentException("Not implemented");
    }

    default SoundEvent getSwimSound0() {
        throw new IllegalArgumentException("Not implemented");
    }

    default SoundEvent getSwimSplashSound0() {
        throw new IllegalArgumentException("Not implemented");
    }

    default SoundEvent getSwimHighSpeedSplashSound0() {
        throw new IllegalArgumentException("Not implemented");
    }

    default boolean canCollideWithBukkit(Entity entity) {
        throw new IllegalArgumentException("Not implemented");
    }

    default boolean saveAsPassenger(ValueOutput output, boolean includeAll) {
        throw new IllegalArgumentException("Not implemented");
    }

    default void saveWithoutId(ValueOutput output, boolean includeAll) {
        throw new IllegalArgumentException("Not implemented");
    }

    default  void addAdditionalSaveData(ValueOutput output, boolean includeAll) {
        throw new IllegalArgumentException("Not implemented");
    }

    default boolean dropAllLeashConnections(@Nullable Player player, EntityUnleashEvent.UnleashReason reason) {
        throw new IllegalArgumentException("Not implemented");
    }

    default CraftPortalEvent callPortalEvent(Entity entity, Location exit, PlayerTeleportEvent.TeleportCause cause, int searchRadius, int creationRadius) {
        throw new IllegalArgumentException("Not implemented");
    }

    default boolean teleportTo(ServerLevel level, double x, double y, double z, Set<Relative> relatives, float newYRot, float newXRot, boolean resetCamera, org.bukkit.event.player.PlayerTeleportEvent.TeleportCause cause) {
        throw new IllegalArgumentException("Not implemented");
    }

    default void setRemoved(Entity.RemovalReason reason, EntityRemoveEvent.Cause cause) {
        throw new IllegalArgumentException("Not implemented");
    }
}