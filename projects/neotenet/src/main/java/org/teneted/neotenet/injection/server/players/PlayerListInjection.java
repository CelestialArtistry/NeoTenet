package org.teneted.neotenet.injection.server.players;

import com.mojang.authlib.GameProfile;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import net.minecraft.server.players.NameAndId;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public interface PlayerListInjection {

    default Optional<CompoundTag> loadPlayerData(Player nameAndId) {
        throw new IllegalArgumentException("Not implemented");
    }

    default String neotenet$getRemoveMsg() {
        throw new IllegalArgumentException("Not implemented");
    }

    default  @Nullable ServerPlayer canPlayerLogin(ServerLoginPacketListenerImpl serverloginpacketlistenerimpl, GameProfile gameprofile) {
        throw new IllegalArgumentException("Not implemented");
    }

    default boolean disconnectAllPlayersWithProfile(UUID playerId, ServerPlayer player) {
        throw new IllegalArgumentException("Not implemented");
    }

    default ServerPlayer respawn(ServerPlayer serverPlayer, boolean keepAllPlayerData, Entity.RemovalReason removalReason, PlayerRespawnEvent.RespawnReason reason) {
        throw new IllegalArgumentException("Not implemented");
    }

    default ServerPlayer respawn(ServerPlayer serverPlayer, boolean keepAllPlayerData, Entity.RemovalReason removalReason, PlayerRespawnEvent.RespawnReason reason, Location location) {
        throw new IllegalArgumentException("Not implemented");
    }

    default void broadcastAll(Packet packet, Player player) {
        throw new IllegalArgumentException("Not implemented");
    }

    default void broadcastAll(Packet packet, Level level) {
        throw new IllegalArgumentException("Not implemented");
    }

    default void broadcastMessage(Component[] iChatBaseComponents) {
        throw new IllegalArgumentException("Not implemented");
    }

    default ServerStatsCounter getPlayerStats(ServerPlayer entityhuman) {
        throw new IllegalArgumentException("Not implemented");
    }

    default ServerStatsCounter getPlayerStats(NameAndId gameprofile) {
        throw new IllegalArgumentException("Not implemented");
    }

    default void reloadRecipes() {
        throw new IllegalArgumentException("Not implemented");
    }
}
