package org.teneted.neotenet.injection.server.dedicated;

import net.minecraft.server.rcon.RconConsoleSource;

public interface DedicatedServerInjection {

    default String runCommand(RconConsoleSource rconConsoleSource, String s) {
        throw new IllegalArgumentException("Not implemented");
    }
}