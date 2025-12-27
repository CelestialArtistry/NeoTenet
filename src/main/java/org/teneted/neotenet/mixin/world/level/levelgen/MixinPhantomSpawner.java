package org.teneted.neotenet.mixin.world.level.levelgen;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.PhantomSpawner;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PhantomSpawner.class)
public class MixinPhantomSpawner {

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntityWithPassengers(Lnet/minecraft/world/entity/Entity;)V"))
    private void neotenet$pushSpawnReason(ServerLevel p_64576_, boolean p_64577_, boolean p_64578_, CallbackInfoReturnable<Integer> cir) {
        p_64576_.pushAddEntityReason(CreatureSpawnEvent.SpawnReason.NATURAL);
    }
}
