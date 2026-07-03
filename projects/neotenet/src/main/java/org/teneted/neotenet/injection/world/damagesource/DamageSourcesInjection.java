package org.teneted.neotenet.injection.world.damagesource;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public interface DamageSourcesInjection {

    default DamageSource melting() {
        throw new IllegalArgumentException("Not implemented");
    }

    default DamageSource poison() {
        throw new IllegalArgumentException("Not implemented");
    }

    default DamageSource explosion(@Nullable Entity entity, @Nullable Entity entity1, ResourceKey<DamageType> resourceKey) {
        throw new IllegalArgumentException("Not implemented");
    }

    default DamageSource badRespawnPointExplosion(Vec3 vec3, org.bukkit.block.BlockState blockState) {
        throw new IllegalArgumentException("Not implemented");
    }
}
