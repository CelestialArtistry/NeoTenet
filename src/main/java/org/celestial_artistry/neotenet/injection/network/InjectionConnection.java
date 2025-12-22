package org.celestial_artistry.neotenet.injection.network;

import java.net.SocketAddress;

public interface InjectionConnection {

    default SocketAddress getRawAddress() {
        throw new IllegalStateException("Not implemented");
    }
}
