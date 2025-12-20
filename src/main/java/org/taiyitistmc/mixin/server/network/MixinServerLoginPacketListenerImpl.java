package org.taiyitistmc.mixin.server.network;

import com.llamalad7.mixinextras.sugar.Cancellable;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.authlib.GameProfile;

import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.Connection;
import net.minecraft.network.TickablePacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.cookie.ServerboundCookieResponsePacket;
import net.minecraft.network.protocol.login.ServerLoginPacketListener;
import net.minecraft.network.protocol.login.ServerboundLoginAcknowledgedPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import net.minecraft.server.players.PlayerList;
import net.minecraft.util.thread.BlockableEventLoop;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.util.Waitable;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.taiyitistmc.injection.server.network.InjectionServerLoginPacketListenerImpl;

@Mixin(ServerLoginPacketListenerImpl.class)
public abstract class MixinServerLoginPacketListenerImpl implements ServerLoginPacketListener, TickablePacketListener, InjectionServerLoginPacketListenerImpl, CraftPlayer.TransferCookieConnection {

    @Shadow
    @Final
    public Connection connection;
    @Shadow
    @Final
    MinecraftServer server;
    @Shadow
    private ServerPlayer player; // CraftBukkit
    @Shadow
    public abstract void disconnect(Component component);

    @Shadow @Nullable private GameProfile authenticatedProfile;

    @Inject(method = "handleLoginAcknowledgement", at = @At("HEAD"))
    private void taiyitist$ensureThread(ServerboundLoginAcknowledgedPacket packet, CallbackInfo ci) {
        PacketUtils.ensureRunningOnSameThread(packet, this, this.server); // CraftBukkit
    }

    @Inject(method = "handleLoginAcknowledgement", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;setupInboundProtocol(Lnet/minecraft/network/ProtocolInfo;Lnet/minecraft/network/PacketListener;)V"))
    private void taiyitist$setPlayer(ServerboundLoginAcknowledgedPacket p_298815_, CallbackInfo ci, @Local ServerConfigurationPacketListenerImpl listener) {
        listener.player = this.player;
    }

    @Redirect(method = "verifyLoginAndFinishConnectionSetup", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;canPlayerLogin(Ljava/net/SocketAddress;Lcom/mojang/authlib/GameProfile;)Lnet/minecraft/network/chat/Component;"))
    private Component taiyitist$canLogin(PlayerList instance, SocketAddress socketAddress, GameProfile gameProfile, @Local PlayerList playerList) {
        if (this.player == null) {
            this.player = playerList.taiyitist$canPlayerLogin(socketAddress, gameProfile, (ServerLoginPacketListenerImpl) (Object) this);
        }
        return null;
    }

    @Override
    public void disconnect(final String s) {
        this.disconnect(Component.literal(s));
    }

    // CraftBukkit start
    @Override
    public void callPlayerPreLoginEvents(GameProfile gameprofile) throws Exception {
        String playerName = gameprofile.getName();
        java.net.InetAddress address = ((java.net.InetSocketAddress) connection.getRemoteAddress()).getAddress();
        java.util.UUID uniqueId = gameprofile.getId();
        final CraftServer server = this.server.server;

        AsyncPlayerPreLoginEvent asyncEvent = new AsyncPlayerPreLoginEvent(playerName, address, uniqueId);
        server.getPluginManager().callEvent(asyncEvent);

        if (PlayerPreLoginEvent.getHandlerList().getRegisteredListeners().length != 0) {
            final PlayerPreLoginEvent event = new PlayerPreLoginEvent(playerName, address, uniqueId);
            if (asyncEvent.getResult() != PlayerPreLoginEvent.Result.ALLOWED) {
                event.disallow(asyncEvent.getResult(), asyncEvent.getKickMessage());
            }
            Waitable<PlayerPreLoginEvent.Result> waitable = new Waitable<PlayerPreLoginEvent.Result>() {
                @Override
                protected PlayerPreLoginEvent.Result evaluate() {
                    server.getPluginManager().callEvent(event);
                    return event.getResult();
                }
            };

            this.server.processQueue.add(waitable);
            if (waitable.get() != PlayerPreLoginEvent.Result.ALLOWED) {
                disconnect(event.getKickMessage());
            }
        } else {
            if (asyncEvent.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
                disconnect(asyncEvent.getKickMessage());
            }
        }
    }
    // CraftBukkit end

    @Inject(method = "handleCookieResponse", at = @At("HEAD"), cancellable = true)
    private void taiyitist$handleCookie(ServerboundCookieResponsePacket serverboundCookieResponsePacket, CallbackInfo ci) {
        // CraftBukkit start
        PacketUtils.ensureRunningOnSameThread(serverboundCookieResponsePacket, this, (BlockableEventLoop) this.server);
        if (this.player.getBukkitEntity().handleCookieResponse(serverboundCookieResponsePacket)) {
            ci.cancel();
            return;
        }
        // CraftBukkit end
    }
}