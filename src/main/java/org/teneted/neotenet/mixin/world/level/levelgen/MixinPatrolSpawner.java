package org.teneted.neotenet.mixin.world.level.levelgen;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.PatrolSpawner;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PatrolSpawner.class)
public class MixinPatrolSpawner {

    @Inject(method = "spawnPatrolMember", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntityWithPassengers(Lnet/minecraft/world/entity/Entity;)V"))
    private void neotenet$pushSpawnReason(ServerLevel p_224533_, BlockPos p_224534_, RandomSource p_224535_, boolean p_224536_, CallbackInfoReturnable<Boolean> cir) {
        p_224533_.pushAddEntityReason(CreatureSpawnEvent.SpawnReason.PATROL);
    }
}
