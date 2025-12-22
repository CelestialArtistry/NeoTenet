package org.celestial_artistry.neotenet.mixin.server;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerTickRateManager;
import net.minecraft.util.TimeUtil;
import net.minecraft.world.TickRateManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerTickRateManager.class)
public abstract class MixinServerTickRateManager extends TickRateManager {


    @Shadow
    private long remainingSprintTicks;

    @Shadow
    private long scheduledCurrentSprintTicks;

    @Shadow
    private long sprintTimeSpend;

    @Shadow
    @Final
    private MinecraftServer server;

    @Shadow
    public abstract void setFrozen(boolean p_309002_);

    @Shadow
    private boolean previousIsFrozen;

    public boolean stopSprinting(boolean sendLog) {
        if (this.remainingSprintTicks > 0L) {
            this.finishTickSprint(sendLog);
            return true;
        } else {
            return false;
        }
    }

    private void finishTickSprint(boolean sendLog) { // CraftBukkit - add sendLog parameter
        long i = this.scheduledCurrentSprintTicks - this.remainingSprintTicks;
        double d0 = Math.max(1.0, (double)this.sprintTimeSpend) / (double) TimeUtil.NANOSECONDS_PER_MILLISECOND;
        int j = (int)((double)(TimeUtil.MILLISECONDS_PER_SECOND * i) / d0);
        String s = String.format("%.2f", i == 0L ? (double)this.millisecondsPerTick() : d0 / (double)i);
        this.scheduledCurrentSprintTicks = 0L;
        this.sprintTimeSpend = 0L;
        // CraftBukkit start - add sendLog parameter
        if (sendLog) {
            this.server.createCommandSourceStack().sendSuccess(() -> Component.translatable("commands.tick.sprint.report", j, s), true);
        }
        // CraftBukkit end
        this.remainingSprintTicks = 0L;
        this.setFrozen(this.previousIsFrozen);
        this.server.onTickRateChanged();
    }
}
