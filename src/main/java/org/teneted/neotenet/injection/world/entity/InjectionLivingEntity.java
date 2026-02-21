package org.teneted.neotenet.injection.world.entity;

import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.jetbrains.annotations.Nullable;

public interface InjectionLivingEntity extends InjectionEntity {

    default void equipEventAndSound(EquipmentSlot slot, ItemStack oldItem, ItemStack newItem, boolean silent) {
        throw new IllegalStateException("Not implemented");
    }

    default Optional<EntityPotionEffectEvent.Cause> getEffectCause() {
        return Optional.empty();
    }

    default void pushHealReason(EntityRegainHealthEvent.RegainReason reason) {
        throw new IllegalStateException("Not implemented");
    }

    default void pushEffectCause(EntityPotionEffectEvent.Cause cause) {
        throw new IllegalStateException("Not implemented");
    }

    default void onEquipItem(EquipmentSlot enumitemslot, ItemStack itemstack, ItemStack itemstack1, boolean silent) {
        throw new IllegalStateException("Not implemented");
    }

    default boolean removeAllEffects(EntityPotionEffectEvent.Cause cause) {
        throw new IllegalStateException("Not implemented");
    }

    default boolean addEffect(MobEffectInstance mobeffect, EntityPotionEffectEvent.Cause cause) {
        throw new IllegalStateException("Not implemented");
    }

    default boolean addEffect(MobEffectInstance mobeffect, @Nullable Entity entity, EntityPotionEffectEvent.Cause cause) {
        throw new IllegalStateException("Not implemented");
    }

    @Nullable
    default MobEffectInstance c(@Nullable MobEffect mobeffectlist, EntityPotionEffectEvent.Cause cause) {
        throw new IllegalStateException("Not implemented");
    }

    default boolean removeEffect(Holder<MobEffect> holder, EntityPotionEffectEvent.Cause cause) {
        throw new IllegalStateException("Not implemented");
    }

    default void heal(float f, EntityRegainHealthEvent.RegainReason regainReason) {
        throw new IllegalStateException("Not implemented");
    }

    default int getExpReward(@Nullable Entity entity) {
        throw new IllegalStateException("Not implemented");
    }

    default SoundEvent getHurtSound0(DamageSource damagesource) {
        throw new IllegalStateException("Not implemented");
    }

    default SoundEvent getDeathSound0() {
        throw new IllegalStateException("Not implemented");
    }

    default SoundEvent getFallDamageSound0(int fallHeight) {
        throw new IllegalStateException("Not implemented");
    }

    default SoundEvent getDrinkingSound0(ItemStack itemstack) {
        throw new IllegalStateException("Not implemented");
    }

    default SoundEvent getEatingSound0(ItemStack itemstack) {
        throw new IllegalStateException("Not implemented");
    }

    default void setArrowCount(int i, boolean flag) {
        throw new IllegalStateException("Not implemented");
    }

    default void setItemSlot(EquipmentSlot enumitemslot, ItemStack itemstack, boolean silent) {
        throw new IllegalStateException("Not implemented");
    }

    default boolean actuallyHurtBukkit(DamageSource p_21240_, float p_21241_, EntityDamageEvent event) {
        throw new IllegalStateException("Not implemented");
    }

    default MobEffectInstance removeEffectNoUpdate(Holder<MobEffect> holder, EntityPotionEffectEvent.Cause cause) {
        throw new IllegalStateException("Not implemented");
    }
}
