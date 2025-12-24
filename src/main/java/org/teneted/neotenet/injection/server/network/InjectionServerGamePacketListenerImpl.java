package org.teneted.neotenet.injection.server.network;

import java.net.SocketAddress;
import java.util.Set;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.world.entity.RelativeMovement;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.player.PlayerTeleportEvent;

public interface InjectionServerGamePacketListenerImpl extends InjectionServerCommonPacketListenerImpl {

    default CraftPlayer getCraftPlayer() {
        throw new IllegalStateException("Not implemented");
    }

    default boolean teleport(double d0, double d1, double d2, float f, float f1, PlayerTeleportEvent.TeleportCause cause) {
        throw new IllegalStateException("Not implemented");
    }

    default boolean teleport(double d0, double d1, double d2, float f, float f1, Set<RelativeMovement> set, PlayerTeleportEvent.TeleportCause cause) { // CraftBukkit - Return event status
        throw new IllegalStateException("Not implemented");
    }

    default void teleport(Location dest) {
        throw new IllegalStateException("Not implemented");
    }

    default void internalTeleport(double d0, double d1, double d2, float f, float f1, Set<RelativeMovement> set) {
        throw new IllegalStateException("Not implemented");
    }

    default void chat(String s, PlayerChatMessage original, boolean async) {
        throw new IllegalStateException("Not implemented");
    }

    default void handleCommand(String s) {
        throw new IllegalStateException("Not implemented");
    }

    default boolean isDisconnected() {
        throw new IllegalStateException("Not implemented");
    }

    default boolean checkLimit(long timestamp) {
        throw new IllegalStateException("Not implemented");
    }

    default void pushTeleportCause(PlayerTeleportEvent.TeleportCause cause) {
        throw new IllegalStateException("Not implemented");
    }

    default void detectRateSpam(String s) {
        throw new IllegalStateException("Not implemented");
    }

    default public SocketAddress getRawAddress() {
        throw new IllegalStateException("Not implemented");
    }
}
