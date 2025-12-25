package org.teneted.neotenet.mixin.world.level.block.entity.trialspawner;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerData;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TrialSpawnerData.class)
public class MixinTrialSpawnerData {

    @Inject(method = "lambda$resetAfterBecomingOminous$10", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;remove(Lnet/minecraft/world/entity/Entity$RemovalReason;)V"))
    private static void neotenet$addRemoveCause(ServerLevel p_338185_, Entity p_351984_, CallbackInfo ci) {
        p_351984_.pushRemoveCause(EntityRemoveEvent.Cause.DESPAWN);
    }
}
