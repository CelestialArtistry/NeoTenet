package org.teneted.neotenet.mixin.network.protocol;

import net.minecraft.ReportedException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.server.RunningOnDifferentThreadException;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.util.thread.BlockableEventLoop;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PacketUtils.class)
public abstract class MixinPacketUtils {

    @Shadow
    @Final
    private static Logger LOGGER;

    @Shadow
    public static <T extends PacketListener> ReportedException makeReportedException(Exception p_341646_, Packet<T> p_341629_, T p_341619_) {
        return null;
    }

    /**
     * @author wdog5
     * @reason bukkit reason
     */
    @Overwrite
    public static <T extends PacketListener> void ensureRunningOnSameThread(Packet<T> packet, T processor, BlockableEventLoop<?> executor) throws RunningOnDifferentThreadException {
        if (!executor.isSameThread()) {
            executor.executeIfPossible(() -> {
                if (MinecraftServer.getServer().hasStopped() || (processor instanceof ServerGamePacketListenerImpl && ((ServerGamePacketListenerImpl) processor).processedDisconnect)) return; // CraftBukkit, MC-142590
                if (processor.isAcceptingMessages()) {
                    try {
                        packet.handle(processor);
                    } catch (Exception exception) {
                        if (exception instanceof ReportedException reportedexception && reportedexception.getCause() instanceof OutOfMemoryError) {
                            throw makeReportedException(exception, packet, processor);
                        }

                        LOGGER.error("Failed to handle packet {}, suppressing error", packet, exception);
                    }
                } else {
                    LOGGER.debug("Ignoring packet due to disconnection: {}", packet);
                }

            });
            throw RunningOnDifferentThreadException.RUNNING_ON_DIFFERENT_THREAD;
            // CraftBukkit start - SPIGOT-5477, MC-142590
        } else if (MinecraftServer.getServer().hasStopped() || (processor instanceof ServerGamePacketListenerImpl && ((ServerGamePacketListenerImpl) processor).processedDisconnect)) {
            throw RunningOnDifferentThreadException.RUNNING_ON_DIFFERENT_THREAD;
            // CraftBukkit end
        }
    }
}
