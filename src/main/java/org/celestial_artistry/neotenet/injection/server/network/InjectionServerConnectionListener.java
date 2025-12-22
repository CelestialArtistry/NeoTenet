package org.celestial_artistry.neotenet.injection.server.network;

public interface InjectionServerConnectionListener {

    default void acceptConnections() {
        throw new IllegalStateException("Not implemented");
    }
}
