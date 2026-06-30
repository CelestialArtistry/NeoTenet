package org.teneted.neotenet.injection.world.entity.player;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.player.Player;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.event.entity.EntityExhaustionEvent;
import org.teneted.neotenet.injection.world.entity.LivingEntityInjection;

public interface PlayerInjection extends LivingEntityInjection {

    @Override
    default CraftHumanEntity getBukkitEntity() {
        throw new IllegalArgumentException("Not implemented");
    }

    default Either<Player.BedSleepingProblem, Unit> startSleepInBed(BlockPos pos, boolean force) {
        throw new IllegalArgumentException("Not implemented");
    }

    default void causeFoodExhaustion(float f, EntityExhaustionEvent.ExhaustionReason reason) {
        throw new IllegalArgumentException("Not implemented");
    }
}
