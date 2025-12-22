package org.celestial_artistry.neotenet.mixin.world.entity.monster;

import java.util.UUID;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.world.entity.monster.ZombieVillager.class)
public abstract class MixinZombieVillager extends Zombie {

    public MixinZombieVillager(EntityType<? extends Zombie> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "startConverting", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/ZombieVillager;removeEffect(Lnet/minecraft/core/Holder;)Z"))
    private void neotenet$convert1(UUID conversionStarterIn, int conversionTimeIn, CallbackInfo ci) {
        this.persist = true;
        pushEffectCause(EntityPotionEffectEvent.Cause.CONVERSION);
    }

    @Inject(method = "startConverting", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/ZombieVillager;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z"))
    private void neotenet$convert2(UUID conversionStarterIn, int conversionTimeIn, CallbackInfo ci) {
        pushEffectCause(EntityPotionEffectEvent.Cause.CONVERSION);
    }
}
