package org.teneted.neotenet.injection.server.level;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Predicate;

public interface ServerEntity_SynchronizerInjection {

    default void sendToTrackingPlayersFilteredAndSelf(Packet<? super ClientGamePacketListener> packet, Predicate<ServerPlayer> predicate) {
        throw new IllegalArgumentException("Not implemented");
    }
}