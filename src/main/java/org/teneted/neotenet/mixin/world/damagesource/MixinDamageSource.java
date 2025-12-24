package org.teneted.neotenet.mixin.world.damagesource;

import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.teneted.neotenet.injection.world.damagesource.InjectionDamageSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;

@Mixin(DamageSource.class)
public class MixinDamageSource implements InjectionDamageSource {

    // CraftBukkit start
    @Nullable
    @Shadow
    private org.bukkit.block.Block directBlock; // The block that caused the damage. damageSourcePosition is not used for all block damages
    @Nullable
    @Shadow
    private org.bukkit.block.BlockState directBlockState; // The block state of the block relevant to this damage source
    @Shadow
    private boolean sweep = false;
    @Shadow
    private boolean melting = false;
    @Shadow
    private boolean poison = false;
    @Shadow
    private Entity customEntityDamager = null; // This field is a helper for when direct entity damage is not set by vanilla
    @Shadow
    private Entity customCausingEntityDamager = null; // This field is a helper for when causing entity damage is not set by vanilla
    @Shadow
    @Final
    private Entity directEntity;
    @Shadow
    @Final
    private Entity causingEntity;
    @Shadow
    @Final
    private Holder<DamageType> type;
    @Shadow
    @Final
    private Vec3 damageSourcePosition;

    @Override
    public DamageSource sweep() {
        this.sweep = true;
        return ((DamageSource) (Object) this);
    }

    @Override
    public boolean isSweep() {
        return this.sweep;
    }

    @Override
    public DamageSource melting() {
        this.melting = true;
        return ((DamageSource) (Object) this);
    }

    @Override
    public boolean isMelting() {
        return this.melting;
    }

    @Override
    public DamageSource poison() {
        this.poison = true;
        return ((DamageSource) (Object) this);
    }

    @Override
    public boolean isPoison() {
        return this.poison;
    }

    @Override
    public Entity getDamager() {
        return (this.customEntityDamager != null) ? this.customEntityDamager : this.directEntity;
    }

    @Override
    public Entity getCausingDamager() {
        return (this.customCausingEntityDamager != null) ? this.customCausingEntityDamager : this.causingEntity;
    }

    @Override
    public DamageSource customEntityDamager(Entity entity) {
        // This method is not intended for change the causing entity if is already set
        // also is only necessary if the entity passed is not the direct entity or different from the current causingEntity
        if (this.customEntityDamager != null || this.directEntity == entity || this.causingEntity == entity) {
            return ((DamageSource) (Object) this);
        }
        DamageSource damageSource = this.cloneInstance();
        damageSource.customEntityDamager = entity;
        return damageSource;
    }

    @Override
    public DamageSource customCausingEntityDamager(Entity entity) {
        // This method is not intended for change the causing entity if is already set
        // also is only necessary if the entity passed is not the direct entity or different from the current causingEntity
        if (this.customCausingEntityDamager != null || this.directEntity == entity || this.causingEntity == entity) {
            return ((DamageSource) (Object) this);
        }
        DamageSource damageSource = this.cloneInstance();
        damageSource.customCausingEntityDamager = entity;
        return damageSource;
    }

    @Override
    public org.bukkit.block.Block getDirectBlock() {
        return this.directBlock;
    }

    @Override
    public DamageSource directBlock(net.minecraft.world.level.Level world, net.minecraft.core.BlockPos blockPosition) {
        if (blockPosition == null || world == null) {
            return ((DamageSource) (Object) this);
        }
        return directBlock(org.bukkit.craftbukkit.block.CraftBlock.at(world, blockPosition));
    }

    @Override
    public DamageSource directBlock(org.bukkit.block.Block block) {
        if (block == null) {
            return ((DamageSource) (Object) this);
        }
        // Cloning the instance lets us return unique instances of DamageSource without affecting constants defined in DamageSources
        DamageSource damageSource = this.cloneInstance();
        damageSource.directBlock = block;
        return damageSource;
    }

    @Override
    public org.bukkit.block.BlockState getDirectBlockState() {
        return this.directBlockState;
    }

    @Override
    public DamageSource directBlockState(org.bukkit.block.BlockState blockState) {
        if (blockState == null) {
            return ((DamageSource) (Object) this);
        }
        // Cloning the instance lets us return unique instances of DamageSource without affecting constants defined in DamageSources
        DamageSource damageSource = this.cloneInstance();
        damageSource.directBlockState = blockState;
        return damageSource;
    }

    @Override
    public DamageSource cloneInstance() {
        DamageSource damageSource = new DamageSource(this.type, this.directEntity, this.causingEntity, this.damageSourcePosition);
        damageSource.directBlock = this.getDirectBlock();
        damageSource.directBlockState = this.getDirectBlockState();
        damageSource.sweep = this.isSweep();
        damageSource.poison = this.isPoison();
        damageSource.melting = this.isMelting();
        return damageSource;
    }
    // CraftBukkit end
}
