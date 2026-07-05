package org.teneted.neotenet.mixin.core.stats;

import net.minecraft.stats.Stat;
import net.minecraft.stats.StatsCounter;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StatsCounter.class)
public abstract class StatsCounterMixin {

    @Shadow
    public abstract int getValue(Stat<?> stat);

    @Inject(method = "increment", at = @At(value = "INVOKE", target = "Lnet/minecraft/stats/StatsCounter;setValue(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/stats/Stat;I)V"), cancellable = true)
    private void neotenet$handleStatisticsIncrease(Player player, Stat<?> stat, int count, CallbackInfo ci) {
        // CraftBukkit start - fire Statistic events
        org.bukkit.event.Cancellable cancellable = org.bukkit.craftbukkit.event.CraftEventFactory.handleStatisticsIncrease(player, stat, this.getValue(stat), count);
        if (cancellable != null && cancellable.isCancelled()) {
            ci.cancel();
            return;
        }
        // CraftBukkit end
    }
}
