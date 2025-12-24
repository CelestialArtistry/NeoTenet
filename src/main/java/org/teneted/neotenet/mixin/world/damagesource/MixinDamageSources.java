package org.teneted.neotenet.mixin.world.damagesource;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.teneted.neotenet.injection.world.damagesource.InjectionDamageSources;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DamageSources.class)
public abstract class MixinDamageSources implements InjectionDamageSources {

    @Shadow
    private DamageSource melting;
    @Shadow
    private DamageSource poison;

    @Shadow
    public abstract DamageSource source(ResourceKey<DamageType> p_270957_);

    @Shadow
    @Final
    private Registry<DamageType> damageTypes;

    @Shadow
    protected abstract DamageSource source(ResourceKey<DamageType> p_270076_, @Nullable Entity p_270656_, @Nullable Entity p_270242_);

    @Inject(method = "<init>", at = @At("RETURN"))
    private void neotenet$init(RegistryAccess p_270740_, CallbackInfo ci) {
        this.melting = this.source(DamageTypes.ON_FIRE).melting();
        this.poison = this.source(DamageTypes.MAGIC).poison();
        // CraftBukkit end
    }

    // CraftBukkit start
    @Override
    public DamageSource melting() {
        return this.melting;
    }

    @Override
    public DamageSource poison() {
        return this.poison;
    }
    // CraftBukkit end

    @Override
    public DamageSource explosion(@Nullable Entity entity, @Nullable Entity entity1, ResourceKey<DamageType> resourceKey) {
        return this.source(resourceKey, entity, entity1);
    }

    @Override
    public DamageSource badRespawnPointExplosion(Vec3 vec3d, org.bukkit.block.BlockState blockState) {
        return new DamageSource(this.damageTypes.getHolderOrThrow(DamageTypes.BAD_RESPAWN_POINT), vec3d).directBlockState(blockState);
    }
}
