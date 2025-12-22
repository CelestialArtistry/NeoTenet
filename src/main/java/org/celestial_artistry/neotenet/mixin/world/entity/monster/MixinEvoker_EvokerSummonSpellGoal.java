package org.celestial_artistry.neotenet.mixin.world.entity.monster;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.world.entity.monster.Evoker$EvokerSummonSpellGoal")
public class MixinEvoker_EvokerSummonSpellGoal {

    @Inject(method = "performSpellCasting", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntityWithPassengers(Lnet/minecraft/world/entity/Entity;)V"))
    private void neotenet$reason(CallbackInfo ci, @Local ServerLevel level) {
        level.pushAddEntityReason(CreatureSpawnEvent.SpawnReason.SPELL);
    }
}
