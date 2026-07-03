package org.teneted.neotenet.injection.world.clock;

import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.clock.ClockTimeMarker;
import net.minecraft.world.clock.WorldClock;
import org.bukkit.event.world.TimeSkipEvent;

public interface ServerClockManagerInjection {

    default void init(ServerLevel server) {
        throw new IllegalArgumentException("Not implemented");
    }

    default void setTotalTicks(Holder<WorldClock> clock, long totalTicks, TimeSkipEvent.SkipReason reason) {
        throw new IllegalArgumentException("Not implemented");
    }

    default boolean moveToTimeMarker(Holder<WorldClock> clock, ResourceKey<ClockTimeMarker> timeMarkerId, TimeSkipEvent.SkipReason reason) {
        throw new IllegalArgumentException("Not implemented");
    }

    default ClientboundSetTimePacket createFullSyncPacket(ServerPlayer player) {
        throw new IllegalArgumentException("Not implemented");
    }
}
