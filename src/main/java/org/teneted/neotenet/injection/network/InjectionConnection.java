package org.teneted.neotenet.injection.network;

import java.net.SocketAddress;

public interface InjectionConnection {

    default SocketAddress getRawAddress() {
        throw new IllegalStateException("Not implemented");
    }
}
