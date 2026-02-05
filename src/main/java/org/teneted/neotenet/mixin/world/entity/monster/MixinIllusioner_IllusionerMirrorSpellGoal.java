package org.teneted.neotenet.mixin.world.entity.monster;

import net.minecraft.world.entity.monster.Illusioner;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.world.entity.monster.Illusioner.IllusionerMirrorSpellGoal")
public class MixinIllusioner_IllusionerMirrorSpellGoal {

    @Shadow
    @Final
    private Illusioner this$0;

    @Inject(method = "performSpellCasting", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/Illusioner;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z"))
    private void neotenet$pushEffectCause(CallbackInfo ci) {
        this$0.pushEffectCause(EntityPotionEffectEvent.Cause.ILLUSION);
    }
}
