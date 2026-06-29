package org.teneted.neotenet.injection.network.network.protocol.common.custom;

public interface DiscardedPayloadInjection {

    default io.netty.buffer.ByteBuf data() {
        throw new IllegalArgumentException("Not implemented");
    }

    default void bridge$setData(io.netty.buffer.ByteBuf data) {
        throw new IllegalArgumentException("Not implemented");
    }
}
