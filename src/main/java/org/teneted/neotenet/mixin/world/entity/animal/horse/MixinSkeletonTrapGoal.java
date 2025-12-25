package org.teneted.neotenet.mixin.world.entity.animal.horse;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.horse.SkeletonTrapGoal;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SkeletonTrapGoal.class)
public class MixinSkeletonTrapGoal {

    @Redirect(method = "lambda$tick$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean neotenet$striking(ServerLevel instance, Entity p_8837_) {
        instance.pushAddEntityReason(CreatureSpawnEvent.SpawnReason.TRAP);
        return instance.strikeLightning(p_8837_);
    }

    @Inject(method = "lambda$tick$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntityWithPassengers(Lnet/minecraft/world/entity/Entity;)V", ordinal = 0))
    private void neotenet$pushReason0(ServerLevel serverlevel, CallbackInfo ci) {
        serverlevel.pushAddEntityReason(CreatureSpawnEvent.SpawnReason.TRAP);
    }

    @Inject(method = "lambda$tick$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntityWithPassengers(Lnet/minecraft/world/entity/Entity;)V", ordinal = 1))
    private void neotenet$pushReason1(ServerLevel serverlevel, CallbackInfo ci) {
        serverlevel.pushAddEntityReason(CreatureSpawnEvent.SpawnReason.JOCKEY);
    }
}
