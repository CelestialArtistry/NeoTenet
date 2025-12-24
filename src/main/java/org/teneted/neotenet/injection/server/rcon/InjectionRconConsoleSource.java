package org.teneted.neotenet.injection.server.rcon;

public interface InjectionRconConsoleSource {

    default void sendMessage(String message) {
        throw new RuntimeException("Not implemented");
    }
}
