package org.teneted.neotenet.mixin.network.connection;

import io.netty.channel.Channel;
import java.net.SocketAddress;
import net.minecraft.network.Connection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.teneted.neotenet.injection.network.InjectionConnection;

@Mixin(Connection.class)
public class MixinConnection implements InjectionConnection {

    @Shadow
    public Channel channel;

    // Spigot Start
    @Override
    public SocketAddress getRawAddress() {
        return this.channel.remoteAddress();
    }
    // Spigot End
}
