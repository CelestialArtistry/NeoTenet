package org.teneted.neotenet.mixin.core.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import net.minecraft.network.Connection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Connection.class)
public class ConnectionMixin {

    @Shadow private boolean disconnectionHandled;
    public String hostname = ""; // CraftBukkit - add field

    @Redirect(method = "disconnect(Lnet/minecraft/network/DisconnectionDetails;)V", at = @At(value = "INVOKE", target = "Lio/netty/channel/Channel;close()Lio/netty/channel/ChannelFuture;"))
    private ChannelFuture neotenet$noDisconnectTwiceWarn(Channel instance) {
        return instance.close();
    }
}
