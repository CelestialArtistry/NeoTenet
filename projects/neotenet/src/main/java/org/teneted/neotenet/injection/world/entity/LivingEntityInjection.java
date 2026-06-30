package org.teneted.neotenet.injection.world.entity;

import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.bukkit.event.entity.EntityKnockbackEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface LivingEntityInjection extends EntityInjection {

    default void onEquipItem(EquipmentSlot slot, ItemStack oldStack, ItemStack stack, boolean silent) {
        throw new IllegalArgumentException("Not implemented");
    }

    default void remove(Entity.RemovalReason reason, EntityRemoveEvent.Cause cause) {
        throw new IllegalArgumentException("Not implemented");
    }

    default @Nullable ItemEntity drop(ItemStack itemStack, boolean randomly, boolean thrownFromHand, boolean callEvent) {
        throw new IllegalArgumentException("Not implemented");
    }

    default boolean removeAllEffects(EntityPotionEffectEvent.Cause cause) {
        throw new IllegalArgumentException("Not implemented");
    }

    default boolean addEffect(MobEffectInstance mobeffectinstance, EntityPotionEffectEvent.Cause cause) {
        throw new IllegalArgumentException("Not implemented");
    }

    default boolean addEffect(MobEffectInstance newEffect, @Nullable Entity source, EntityPotionEffectEvent.Cause cause) {
        throw new IllegalArgumentException("Not implemented");
    }

    default @Nullable MobEffectInstance removeEffectNoUpdate(Holder<MobEffect> effect, EntityPotionEffectEvent.Cause cause) {
        throw new IllegalArgumentException("Not implemented");
    }

    default boolean removeEffect(Holder<MobEffect> holder, EntityPotionEffectEvent.Cause cause) {
        throw new IllegalArgumentException("Not implemented");
    }

    default void heal(float f, EntityRegainHealthEvent.RegainReason regainReason) {
        throw new IllegalArgumentException("Not implemented");
    }

    default void knockback(double power, double xd, double zd, DamageSource source, float damage, boolean comesFromEffect, EntityKnockbackEvent.KnockbackCause cause) {
        throw new IllegalArgumentException("Not implemented");
    }

    default void knockback(double power, double xd, double zd, DamageSource source, float damage, EntityKnockbackEvent.KnockbackCause cause) {
        throw new IllegalArgumentException("Not implemented");
    }

    default SoundEvent getHurtSound0(DamageSource damagesource) {
        throw new IllegalArgumentException("Not implemented");
    }

    default SoundEvent getDeathSound0() {
        throw new IllegalArgumentException("Not implemented");
    }

    default SoundEvent getFallDamageSound0(int fallHeight) {
        throw new IllegalArgumentException("Not implemented");
    }

    default void setArrowCount(int i, boolean flag) {
        throw new IllegalArgumentException("Not implemented");
    }

    default void setItemSlot(EquipmentSlot equipmentslot, ItemStack itemstack, boolean silent) {
        throw new IllegalArgumentException("Not implemented");
    }

    default void detectEquipmentUpdatesPublic() {
        throw new IllegalArgumentException("Not implemented");
    }

    default Optional<Boolean> randomTeleport(double xx, double yy, double zz, boolean showParticles, org.bukkit.event.player.PlayerTeleportEvent.TeleportCause cause) {
        throw new IllegalArgumentException("Not implemented");
    }
}