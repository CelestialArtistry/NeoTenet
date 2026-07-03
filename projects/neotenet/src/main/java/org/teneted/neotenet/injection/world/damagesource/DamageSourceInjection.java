package org.teneted.neotenet.injection.world.damagesource;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;

public interface DamageSourceInjection {

    default DamageSource sweep() {
        throw new IllegalArgumentException("Not implemented");
    }

    default boolean isSweep() {
        throw new IllegalArgumentException("Not implemented");
    }

    default DamageSource melting() {
        throw new IllegalArgumentException("Not implemented");
    }

    default boolean isMelting() {
        throw new IllegalArgumentException("Not implemented");
    }

    default DamageSource poison() {
        throw new IllegalArgumentException("Not implemented");
    }

    default boolean isPoison() {
        throw new IllegalArgumentException("Not implemented");
    }

    default Entity getDamager() {
        throw new IllegalArgumentException("Not implemented");
    }

    default Entity getCausingDamager() {
        throw new IllegalArgumentException("Not implemented");
    }

    default DamageSource customEntityDamager(Entity entity) {
        throw new IllegalArgumentException("Not implemented");
    }

    default DamageSource customCausingEntityDamager(Entity entity) {
        throw new IllegalArgumentException("Not implemented");
    }

    default org.bukkit.block.Block getDirectBlock() {
        throw new IllegalArgumentException("Not implemented");
    }

    default DamageSource directBlock(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos blockPosition) {
        throw new IllegalArgumentException("Not implemented");
    }

    default DamageSource directBlock(org.bukkit.block.Block block) {
        throw new IllegalArgumentException("Not implemented");
    }

    default org.bukkit.block.BlockState getDirectBlockState() {
        throw new IllegalArgumentException("Not implemented");
    }

    default DamageSource directBlockState(org.bukkit.block.BlockState blockState) {
        throw new IllegalArgumentException("Not implemented");
    }

    default DamageSource cloneInstance() {
        throw new IllegalArgumentException("Not implemented");
    }
}
