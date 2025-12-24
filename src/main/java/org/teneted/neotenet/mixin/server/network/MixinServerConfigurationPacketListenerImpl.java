package org.teneted.neotenet.mixin.server.network;

import net.minecraft.network.Connection;
import net.minecraft.network.TickablePacketListener;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.common.ServerboundPongPacket;
import net.minecraft.network.protocol.configuration.ServerConfigurationPacketListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;
import net.neoforged.neoforge.common.extensions.ICommonPacketListener;
import net.neoforged.neoforge.network.registration.NetworkRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerConfigurationPacketListenerImpl.class)
public abstract class MixinServerConfigurationPacketListenerImpl extends ServerCommonPacketListenerImpl implements ServerConfigurationPacketListener, TickablePacketListener {

    public MixinServerConfigurationPacketListenerImpl(MinecraftServer p_295057_, Connection p_294822_, CommonListenerCookie p_301980_) {
        super(p_295057_, p_294822_, p_301980_);
    }

    @Shadow
    private void runConfiguration() {
        throw new AssertionError();
    }

    @Override
    public void handlePong(ServerboundPongPacket packet) {
        ICommonPacketListener self = (ICommonPacketListener) this;

        // Ensure we run on the main server thread
        PacketUtils.ensureRunningOnSameThread(packet, (ServerConfigurationPacketListenerImpl) (Object) this, (ReentrantBlockableEventLoop<?>) self.getMainThreadEventLoop());

        if (packet.getId() == 0) {
            if (!self.getConnectionType().isNeoForge()) {
                if (!NetworkRegistry.initializeOtherConnection((ServerConfigurationPacketListenerImpl) (Object) this)) {
                    return;
                }
            }
            this.runConfiguration();
        }
    }
}
