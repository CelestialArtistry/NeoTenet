package org.teneted.neotenet.mixin.server.network;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;

import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.network.protocol.handshake.ServerHandshakePacketListener;
import net.minecraft.network.protocol.login.ClientboundLoginDisconnectPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerHandshakePacketListenerImpl;
import org.apache.logging.log4j.LogManager;
import org.bukkit.Bukkit;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//TODO fixed
@Mixin(ServerHandshakePacketListenerImpl.class)
public abstract class MixinServerHandshakePacketListenerImpl implements ServerHandshakePacketListener {

    @Shadow
    @Final
    private Connection connection;
    @Shadow
    @Final
    private MinecraftServer server;

    @Shadow
    private static int throttleCounter;

    @Shadow
    @Final
    private static HashMap<InetAddress, Long> throttleTracker;

    @Inject(method = "handleIntention", at = @At("HEAD"))
    private void neotenet$setHostName(ClientIntentionPacket packet, CallbackInfo ci) {
        this.connection.hostname = packet.hostName() + ":" + packet.port(); // CraftBukkit  - set hostname
    }

    @Inject(method = "beginLogin", cancellable = true, at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/network/Connection;setupOutboundProtocol(Lnet/minecraft/network/ProtocolInfo;)V"))
    private void neotenet$throttler(ClientIntentionPacket packet, boolean bl, CallbackInfo ci) {
        try {
            long currentTime = System.currentTimeMillis();
            long connectionThrottle = Bukkit.getServer().getConnectionThrottle();
            InetAddress address = ((InetSocketAddress) this.connection.getRemoteAddress()).getAddress();
            synchronized (throttleTracker) {
                if (throttleTracker.containsKey(address) && !"127.0.0.1".equals(address.getHostAddress()) && currentTime - throttleTracker.get(address) < connectionThrottle) {
                    throttleTracker.put(address, currentTime);
                    var component = Component.literal("Connection throttled! Please wait before reconnecting.");
                    this.connection.send(new ClientboundLoginDisconnectPacket(component));
                    this.connection.disconnect(component);
                    ci.cancel();
                    return;
                }
                throttleTracker.put(address, currentTime);
                ++throttleCounter;
                if (throttleCounter > 200) {
                    throttleCounter = 0;
                    throttleTracker.entrySet().removeIf(entry -> entry.getValue() > connectionThrottle);
                }
            }
        } catch (Throwable t) {
            LogManager.getLogger().debug("Failed to check connection throttle", t);
        }
    }
}
