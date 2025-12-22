package org.celestial_artistry.neotenet.mixin.server.level;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.util.thread.BlockableEventLoop;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerChunkCache.MainThreadExecutor.class)
public abstract class MixinServerChunkCache_MainThreadExecutor extends BlockableEventLoop<Runnable> {

    @Shadow
    @Final
    ServerChunkCache this$0;

    protected MixinServerChunkCache_MainThreadExecutor(String name) {
        super(name);
    }

    /**
     * @author wdog5
     * @reason bukkit
     */
    @Overwrite
    public boolean pollTask() {
        try {
            if (this$0.runDistanceManagerUpdates()) {
                return true;
            } else {
                this$0.getLightEngine().tryScheduleUpdate();
                return super.pollTask();
            }
        } finally {
            this$0.chunkMap.callbackExecutor.run();
            MinecraftServer.getServer().bridge$drainQueuedTasks();
        }
    }
}