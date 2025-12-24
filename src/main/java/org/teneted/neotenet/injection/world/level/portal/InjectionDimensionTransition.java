package org.teneted.neotenet.injection.world.level.portal;

import org.bukkit.event.player.PlayerTeleportEvent;

public interface InjectionDimensionTransition {

    default void setTeleportCause(PlayerTeleportEvent.TeleportCause cause) {
        throw new RuntimeException("Not implemented");
    }

    default PlayerTeleportEvent.TeleportCause getTeleportCause() {
        return PlayerTeleportEvent.TeleportCause.COMMAND;
    }
}
