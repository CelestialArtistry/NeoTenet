package org.teneted.neotenet.injection.world.clock;

import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.clock.ClockNetworkState;
import net.minecraft.world.clock.WorldClock;

public interface ServerClockManager_ClockInstanceInjection {

    default ClockNetworkState packNetworkState(Holder<WorldClock> clock, ServerPlayer player) {
        throw new IllegalArgumentException("Not implemented");
    }
}
