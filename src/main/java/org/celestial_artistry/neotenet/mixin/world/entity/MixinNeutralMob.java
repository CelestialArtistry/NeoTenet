package org.celestial_artistry.neotenet.mixin.world.entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import org.bukkit.event.entity.EntityTargetEvent;
import org.celestial_artistry.neotenet.injection.world.entity.InjectionNeutralMob;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(NeutralMob.class)
public interface MixinNeutralMob extends InjectionNeutralMob {

    @Redirect(method = "readPersistentAngerSaveData", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/NeutralMob;setTarget(Lnet/minecraft/world/entity/LivingEntity;)V", ordinal = 0))
    private void neotenet$setReason0(NeutralMob instance, LivingEntity livingEntity) {
        this.setTarget(livingEntity, EntityTargetEvent.TargetReason.UNKNOWN, false); // CraftBukkit
    }

    @Redirect(method = "readPersistentAngerSaveData", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/NeutralMob;setTarget(Lnet/minecraft/world/entity/LivingEntity;)V", ordinal = 1))
    private void neotenet$setReason1(NeutralMob instance, LivingEntity livingEntity) {
        this.setTarget(livingEntity, EntityTargetEvent.TargetReason.UNKNOWN, false); // CraftBukkit
    }

    @Redirect(method = "stopBeingAngry", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/NeutralMob;setTarget(Lnet/minecraft/world/entity/LivingEntity;)V"))
    private void neotenet$setReason2(NeutralMob instance, LivingEntity livingEntity) {
        this.setTarget((LivingEntity) null, org.bukkit.event.entity.EntityTargetEvent.TargetReason.FORGOT_TARGET, true); // CraftBukkit
    }

    @Override
    boolean setTarget(@Nullable LivingEntity entityliving, org.bukkit.event.entity.EntityTargetEvent.TargetReason reason, boolean fireEvent); // CraftBukkit
}
