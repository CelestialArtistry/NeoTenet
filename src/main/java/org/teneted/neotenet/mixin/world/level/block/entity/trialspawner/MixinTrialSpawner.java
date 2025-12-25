package org.teneted.neotenet.mixin.world.level.block.entity.trialspawner;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawner;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.UUID;

@Mixin(TrialSpawner.class)
public class MixinTrialSpawner {

    @Inject(method = "spawnMob", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;tryAddFreshEntityWithPassengers(Lnet/minecraft/world/entity/Entity;)Z"), cancellable = true)
    private void neotenet$callTrialSpawnerSpawnEvent(ServerLevel p_312582_, BlockPos p_312518_, CallbackInfoReturnable<Optional<UUID>> cir, @Local Entity entity) {
        // CraftBukkit start
        if (org.bukkit.craftbukkit.event.CraftEventFactory.callTrialSpawnerSpawnEvent(entity, p_312518_).isCancelled()) {
            cir.setReturnValue(Optional.empty());
        }
        entity.pushSpawnCause(CreatureSpawnEvent.SpawnReason.TRIAL_SPAWNER);
    }
}
