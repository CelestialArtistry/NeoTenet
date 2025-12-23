package org.celestial_artistry.neotenet.mixin.world.item.enchantment.effects;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.ApplyMobEffect;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ApplyMobEffect.class)
public class MixinApplyMobEffect {

    @Inject(method = "apply", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z"))
    private void neotenet$pushEffectCause(ServerLevel p_345355_, int p_346112_, EnchantedItemInUse p_344766_, Entity p_345996_, Vec3 p_345315_, CallbackInfo ci, @Local(ordinal = 0) LivingEntity livingentity) {
        livingentity.pushEffectCause(org.bukkit.event.entity.EntityPotionEffectEvent.Cause.ATTACK);
    }
}
