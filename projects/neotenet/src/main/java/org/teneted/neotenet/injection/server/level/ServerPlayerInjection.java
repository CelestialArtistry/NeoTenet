package org.teneted.neotenet.injection.server.level;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.TeleportTransition;
import org.bukkit.Location;
import org.bukkit.WeatherType;
import org.bukkit.craftbukkit.event.CraftPortalEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerSpawnChangeEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.Nullable;
import org.teneted.neotenet.injection.world.entity.player.PlayerInjection;

import java.util.Set;

public interface ServerPlayerInjection extends PlayerInjection {

    default void resendItemInHands() {
        throw new IllegalArgumentException("Not implemented");
    }

    default void spawnIn(Level level, boolean flag) {
        throw new IllegalArgumentException("Not implemented");
    }

    default Entity getEntityOnShoulder(CompoundTag tag) {
        throw new IllegalArgumentException("Not implemented");
    }

    default TeleportTransition findRespawnPositionAndUseSpawnBlock(boolean consumeSpawnBlock, TeleportTransition.PostTeleportTransition postTeleportTransition, PlayerRespawnEvent.RespawnReason reason) {
        throw new IllegalArgumentException("Not implemented");
    }

    default Either<Player.BedSleepingProblem, Unit> startSleepInBed(BlockPos pos, boolean force) {
        throw new IllegalArgumentException("Not implemented");
    }

    default int nextContainerCounterInt() {
        throw new IllegalArgumentException("Not implemented");
    }

    default boolean teleportTo(ServerLevel level, double x, double y, double z, Set<Relative> relatives, float newYRot, float newXRot, boolean resetCamera, PlayerTeleportEvent.TeleportCause cause) {
        throw new IllegalArgumentException("Not implemented");
    }

    default void setRespawnPosition(ServerPlayer.@Nullable RespawnConfig respawnConfig, boolean showMessage, PlayerSpawnChangeEvent.Cause cause) {
        throw new IllegalArgumentException("Not implemented");
    }

    default ItemEntity drop(ItemStack itemStack, boolean randomly, boolean thrownFromHand, boolean callEvent) {
        throw new IllegalArgumentException("Not implemented");
    }

    default ItemEntity dropItem(boolean all) {
        throw new IllegalArgumentException("Not implemented");
    }

    default long getPlayerTime(long totalTicks) {
        throw new IllegalArgumentException("Not implemented");
    }

    default WeatherType getPlayerWeather() {
        throw new IllegalArgumentException("Not implemented");
    }

    default void setPlayerWeather(WeatherType type, boolean plugin) {
        throw new IllegalArgumentException("Not implemented");
    }

    default void updateWeather(float oldRain, float newRain, float oldThunder, float newThunder) {
        throw new IllegalArgumentException("Not implemented");
    }

    default void tickWeather() {
        throw new IllegalArgumentException("Not implemented");
    }

    default void resetPlayerWeather() {
        throw new IllegalArgumentException("Not implemented");
    }

    default void forceSetPositionRotation(double x, double y, double z, float yaw, float pitch) {
        throw new IllegalArgumentException("Not implemented");
    }

    default void reset() {
        throw new IllegalArgumentException("Not implemented");
    }
}
