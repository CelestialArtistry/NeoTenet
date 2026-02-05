package org.teneted.neotenet.mixin.world.entity.monster;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.ElderGuardian;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ElderGuardian.class)
public class MixinElderGuardian {

    @Inject(method = "customServerAiStep", at = @At(value = "INVOKE", target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V"))
    private void neotenet$pushPotionEffect(CallbackInfo ci, @Local List<ServerPlayer> list) {
        list.forEach(serverPlayer -> {
            serverPlayer.pushEffectCause(EntityPotionEffectEvent.Cause.ATTACK);
        });
    }
}
