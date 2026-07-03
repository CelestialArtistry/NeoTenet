package org.teneted.neotenet.injection.server.network;

import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.entity.Relative;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Set;

public interface ServerGamePacketListenerImplInjection {

    default boolean teleport(double d0, double d1, double d2, float f, float f1, PlayerTeleportEvent.TeleportCause cause) {
        throw new IllegalArgumentException("Not implemented");
    }

    default boolean teleport(PositionMoveRotation positionmoverotation, Set<Relative> set, PlayerTeleportEvent.TeleportCause cause) {
        throw new IllegalArgumentException("Not implemented");
    }

    default void teleport(Location dest) {
        throw new IllegalArgumentException("Not implemented");
    }

    default void internalTeleport(double d0, double d1, double d2, float f, float f1) {
        throw new IllegalArgumentException("Not implemented");
    }

    default void internalTeleport(PositionMoveRotation destination, Set<Relative> relatives) {
        throw new IllegalArgumentException("Not implemented");
    }

    default void chat(String s, PlayerChatMessage original, boolean async) {
        throw new IllegalArgumentException("Not implemented");
    }
}
