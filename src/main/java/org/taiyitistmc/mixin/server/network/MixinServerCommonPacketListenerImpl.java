package org.taiyitistmc.mixin.server.network;

import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.common.ServerCommonPacketListener;
import net.minecraft.network.protocol.common.ServerboundResourcePackPacket;
import net.minecraft.network.protocol.cookie.ServerboundCookieResponsePacket;
import net.minecraft.network.protocol.game.ClientboundSetDefaultSpawnPositionPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.taiyitistmc.injection.server.network.InjectionServerCommonPacketListenerImpl;

@Mixin(ServerCommonPacketListenerImpl.class)
public abstract class MixinServerCommonPacketListenerImpl implements ServerCommonPacketListener, InjectionServerCommonPacketListenerImpl {

    @Shadow
    @Final
    private static Logger LOGGER;
    public boolean processedDisconnect;
    @Shadow
    @Final
    protected Connection connection;
    // @formatter:on
    @Shadow
    @Final
    protected MinecraftServer server;
    @Shadow
    @Final
    private boolean transferred;

    @Shadow
    public abstract void send(Packet<?> p_300558_);

    @Shadow
    protected abstract boolean isSingleplayerOwner();

    @Shadow
    public abstract void onDisconnect(DisconnectionDetails disconnectionDetails);

    @Shadow
    public abstract void disconnect(Component component);

    @Shadow
    protected ServerPlayer player;

    @Shadow
    protected CraftServer cserver;

    @ModifyConstant(method = "keepConnectionAlive", constant = @Constant(longValue = 15000L))
    private long taiyitist$incrKeepaliveTimeout(long l) {
        return 25000L;
    }

    public final boolean isDisconnected() {
        return !this.player.joining && !this.connection.isConnected();
    }


    @Override
    public void disconnect(String s) {
        this.disconnect(Component.literal(s));
    }

    @Inject(method = "send(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketSendListener;)V", cancellable = true, at = @At("HEAD"))
    private void taiyitist$updateCompassTarget(Packet<?> packetIn, PacketSendListener futureListeners, CallbackInfo ci) {
        if (packetIn == null || processedDisconnect) {
            ci.cancel();
            return;
        }
        if (packetIn instanceof ClientboundSetDefaultSpawnPositionPacket packet6) {
            this.player.compassTarget = new Location(this.getCraftPlayer().getWorld(), packet6.pos.getX(), packet6.pos.getY(), packet6.pos.getZ());
        }
    }

    @Inject(method = "handleResourcePackResponse", at = @At("RETURN"))
    private void taiyitist$handleResourcePackStatus(ServerboundResourcePackPacket packetIn, CallbackInfo ci) {
        this.cserver.getPluginManager().callEvent(new PlayerResourcePackStatusEvent(this.getCraftPlayer(), packetIn.id(), PlayerResourcePackStatusEvent.Status.values()[packetIn.action().ordinal()]));
    }

    @Inject(method = "handleCookieResponse", cancellable = true, at = @At("HEAD"))
    private void taiyitist$handleCookie(ServerboundCookieResponsePacket serverboundCookieResponsePacket, CallbackInfo ci) {
        PacketUtils.ensureRunningOnSameThread(serverboundCookieResponsePacket, (ServerCommonPacketListenerImpl) (Object) this, this.server);
        if (this.player.getBukkitEntity().handleCookieResponse(serverboundCookieResponsePacket)) {
            ci.cancel();
        }
    }

    @Override
    public boolean isTransferred() {
        return this.transferred;
    }

    @Override
    public ConnectionProtocol getProtocol() {
        return this.protocol();
    }

    @Override
    public void sendPacket(Packet<?> packet) {
        this.send(packet);
    }

    @Override
    public void kickPlayer(Component component) {
        disconnect(component);
    }

    @Override
    public CraftPlayer getCraftPlayer() {
        return (this.player == null) ? null : (CraftPlayer) this.player.getBukkitEntity();
        // CraftBukkit end
    }
}
