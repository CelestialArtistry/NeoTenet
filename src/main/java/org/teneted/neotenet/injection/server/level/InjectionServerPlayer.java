package org.teneted.neotenet.injection.server.level;

import net.minecraft.world.level.portal.DimensionTransition;
import org.bukkit.Location;
import org.bukkit.craftbukkit.event.CraftPortalEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.teneted.neotenet.injection.world.entity.player.InjectionPlayer;
import com.mojang.datafixers.util.Either;

import java.util.Optional;

import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.border.WorldBorder;
import org.bukkit.WeatherType;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.player.PlayerSpawnChangeEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.Nullable;

public interface InjectionServerPlayer extends InjectionPlayer {

    @Override
    default CraftPlayer getBukkitEntity() {
        throw new IllegalStateException("Not implemented");
    }

    default int nextContainerCounterInt() {
        throw new IllegalStateException("Not implemented");
    }

    default BlockPos getSpawnPoint(ServerLevel worldserver) {
        throw new IllegalStateException("Not implemented");
    }

    default void spawnIn(Level world) {
        throw new IllegalStateException("Not implemented");
    }

    default Entity changeDimension(ServerLevel worldserver, PlayerTeleportEvent.TeleportCause cause) {
        throw new IllegalStateException("Not implemented");
    }

    default Optional<BlockUtil.FoundRectangle> getExitPortal(ServerLevel worldserver, BlockPos blockposition, boolean flag, WorldBorder worldborder, int searchRadius, boolean canCreatePortal, int createRadius) { // CraftBukkit
        throw new IllegalStateException("Not implemented");
    }

    default Either<Player.BedSleepingProblem, Unit> getBedResult(BlockPos blockposition, Direction enumdirection) {
        throw new IllegalStateException("Not implemented");
    }

    default void teleportTo(ServerLevel worldserver, double d0, double d1, double d2, float f, float f1, PlayerTeleportEvent.TeleportCause cause) {
        throw new IllegalStateException("Not implemented");
    }

    default void setRespawnPosition(ResourceKey<Level> resourcekey, @Nullable BlockPos blockposition, float f, boolean flag, boolean flag1, PlayerSpawnChangeEvent.Cause cause) {
        throw new IllegalStateException("Not implemented");
    }

    default long getPlayerTime() {
        throw new IllegalStateException("Not implemented");
    }

    default WeatherType getPlayerWeather() {
        throw new IllegalStateException("Not implemented");
    }

    default void setPlayerWeather(WeatherType type, boolean plugin) {
        throw new IllegalStateException("Not implemented");
    }

    default void updateWeather(float oldRain, float newRain, float oldThunder, float newThunder) {
        throw new IllegalStateException("Not implemented");
    }

    default void tickWeather() {
        throw new IllegalStateException("Not implemented");
    }

    default void resetPlayerWeather() {
        throw new IllegalStateException("Not implemented");
    }

    default void forceSetPositionRotation(double x, double y, double z, float yaw, float pitch) {
        throw new IllegalStateException("Not implemented");
    }

    default void reset() {
        throw new IllegalStateException("Not implemented");
    }

    default void pushChangeDimensionCause(PlayerTeleportEvent.TeleportCause cause) {
        throw new IllegalStateException("Not implemented");
    }

    default void pushChangeSpawnCause(PlayerSpawnChangeEvent.Cause cause) {
        throw new IllegalStateException("Not implemented");
    }

    default void pushRespawnCause(PlayerRespawnEvent.RespawnReason reason) {
        throw new IllegalStateException("Not implemented");
    }

    default void resendItemInHands() {
        throw new IllegalStateException("Not implemented");
    }

    default DimensionTransition findRespawnPositionAndUseSpawnBlock(boolean p_348590_, DimensionTransition.PostDimensionTransition p_352261_, PlayerRespawnEvent.RespawnReason reason) {
        throw new IllegalStateException("Not implemented");
    }

    default CraftPortalEvent callPortalEvent(Entity entity, Location exit, PlayerTeleportEvent.TeleportCause cause, int searchRadius, int creationRadius) {
        throw new IllegalStateException("Not implemented");
    }
}
