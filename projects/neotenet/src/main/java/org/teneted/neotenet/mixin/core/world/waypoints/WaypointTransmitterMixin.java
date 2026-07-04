package org.teneted.neotenet.mixin.core.world.waypoints;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.waypoints.WaypointTransmitter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WaypointTransmitter.class)
public interface WaypointTransmitterMixin {

    @Inject(method = "doesSourceIgnoreReceiver", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;isSpectator()Z"), cancellable = true)
    private static void neotenet$canSee(LivingEntity source, ServerPlayer receiver, CallbackInfoReturnable<Boolean> cir) {
        // CraftBukkit start
        if (!receiver.getBukkitEntity().canSee(source.getBukkitEntity())) {
            cir.setReturnValue(true);
        }
        // CraftBukkit end
    }
}
