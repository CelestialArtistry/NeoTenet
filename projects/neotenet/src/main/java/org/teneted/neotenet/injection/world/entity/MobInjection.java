package org.teneted.neotenet.injection.world.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.ConversionParams;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTransformEvent;
import org.jetbrains.annotations.Nullable;

public interface MobInjection extends LivingEntityInjection {

    default boolean setTarget(LivingEntity target, EntityTargetEvent.TargetReason reason, boolean fireEvent) {
        throw new IllegalArgumentException("Not implemented");
    }

    default SoundEvent getAmbientSound0() {
        throw new IllegalArgumentException("Not implemented");
    }

    default ItemStack equipItemIfPossible(ServerLevel level, ItemStack itemStack, ItemEntity itementity) {
        throw new IllegalArgumentException("Not implemented");
    }

    default <T extends Mob> @Nullable T convertTo(EntityType<T> entityType, ConversionParams params, EntitySpawnReason spawnReason, ConversionParams.AfterConversion<T> afterConversion, EntityTransformEvent.TransformReason transformReason, CreatureSpawnEvent.SpawnReason bukkitSpawnReason) {
        throw new IllegalArgumentException("Not implemented");
    }

    default <T extends Mob> @Nullable T convertTo(EntityType<T> entityType, ConversionParams params, ConversionParams.AfterConversion<T> afterConversion, EntityTransformEvent.TransformReason transformReason, CreatureSpawnEvent.SpawnReason spawnReason) {
        throw new IllegalArgumentException("Not implemented");
    }
}
