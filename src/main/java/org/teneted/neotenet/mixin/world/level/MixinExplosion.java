package org.teneted.neotenet.mixin.world.level;

import com.llamalad7.mixinextras.sugar.Cancellable;
import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityKnockbackEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Explosion.class)
public class MixinExplosion {

    @Mutable
    @Shadow
    @Final
    private float radius;

    @Shadow
    public float yield;

    @Shadow
    @Final
    private Explosion.BlockInteraction blockInteraction;

    @Shadow
    @Final
    private DamageSource damageSource;

    @Shadow
    @Final
    private ExplosionDamageCalculator damageCalculator;
    @Shadow
    @Final
    public Entity source;

    @Shadow
    @Final
    private Level level;

    @Shadow
    @Final
    private double x;
    @Shadow
    @Final
    private double y;
    @Shadow
    @Final
    private double z;
    @Shadow
    @Final
    private ObjectArrayList<BlockPos> toBlow;

    @Shadow
    public boolean wasCanceled;

    private Vec3 vec31;


    @Redirect(method = "<init>(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;Lnet/minecraft/world/level/ExplosionDamageCalculator;DDDFZLnet/minecraft/world/level/Explosion$BlockInteraction;Lnet/minecraft/core/particles/ParticleOptions;Lnet/minecraft/core/particles/ParticleOptions;Lnet/minecraft/core/Holder;)V",
            at = @At(value = "FIELD",
                    target = "Lnet/minecraft/world/level/Explosion;radius:F"))
    private void neotenet$resetRadius(Explosion explosion, float radius, @Local(argsOnly = true) float p_46029_) {
        this.radius = (float) Math.max(p_46029_, 0.0); // CraftBukkit - clamp bad values
    }

    @Inject(method = "<init>(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;Lnet/minecraft/world/level/ExplosionDamageCalculator;DDDFZLnet/minecraft/world/level/Explosion$BlockInteraction;Lnet/minecraft/core/particles/ParticleOptions;Lnet/minecraft/core/particles/ParticleOptions;Lnet/minecraft/core/Holder;)V",
            at = @At("RETURN"))
    private void neotenet$setYield(Level p_46024_, Entity p_46025_, DamageSource p_312268_, ExplosionDamageCalculator p_312205_, double p_46026_, double p_46027_, double p_46028_, float p_46029_, boolean p_312333_, Explosion.BlockInteraction p_312294_, ParticleOptions p_312158_, ParticleOptions p_311904_, Holder p_320270_, CallbackInfo ci) {
        this.yield = this.blockInteraction == Explosion.BlockInteraction.DESTROY_WITH_DECAY ? 1.0F / this.radius : 1.0F; // CraftBukkit
    }

    @Inject(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;gameEvent(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/Holder;Lnet/minecraft/world/phys/Vec3;)V"), cancellable = true)
    private void neotenet$checkRadius(CallbackInfo ci) {
        // CraftBukkit start
        if (this.radius < 0.1F) {
            ci.cancel();
            return;
        }
        // CraftBukkit end
    }

    @Redirect(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private boolean neotenet$setLastDamage(Entity instance, DamageSource damageSource, float v, @Cancellable CallbackInfo ci, @Local List<Entity> list) {
        // CraftBukkit start

        // Special case ender dragon only give knockback if no damage is cancelled
        // Thinks to note:
        // - Setting a velocity to a ComplexEntityPart is ignored (and therefore not needed)
        // - Damaging ComplexEntityPart while forward the damage to EntityEnderDragon
        // - Damaging EntityEnderDragon does nothing
        // - EntityEnderDragon hitbock always covers the other parts and is therefore always present
        if (!(instance instanceof EnderDragonPart)) {
            ci.cancel();
        }

        instance.lastDamageCancelled = false;

        if (instance instanceof EnderDragon) {
            for (EnderDragonPart entityComplexPart : ((EnderDragon) instance).subEntities) {
                // Calculate damage separately for each EntityComplexPart
                if (list.contains(entityComplexPart)) {
                    entityComplexPart.hurt(this.damageSource, this.damageCalculator.getEntityDamageAmount(((Explosion) (Object) this), instance));
                }
            }
        } else {
            instance.hurt(this.damageSource, this.damageCalculator.getEntityDamageAmount(((Explosion) (Object) this), instance));
        }

        if (!(instance.lastDamageCancelled)) { // SPIGOT-5339, SPIGOT-6252, SPIGOT-6777: Skip entity if damage event was cancelled
            ci.cancel();
        }
        // CraftBukkit end
        return true;
    }

    @ModifyVariable(
            method = "explode",
            at = @At("STORE"),
            ordinal = 1
    )
    private Vec3 neotenet$callEntityKnockbackEvent(Vec3 vec31) {
        if (this.vec31 == null) return vec31;
        return this.vec31;
    }

    @Inject(method = "explode", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/event/EventHooks;getExplosionKnockback(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/Explosion;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;"))
    private void neotenet$callEntityKnockbackEvent(CallbackInfo ci, @Local Entity entity, @Local(ordinal = 5) double d13, @Local(ordinal = 1) Vec3 vec31) {
        this.vec31 = vec31;
        // CraftBukkit start - Call EntityKnockbackEvent
        if (entity instanceof LivingEntity) {
            Vec3 result = entity.getDeltaMovement().add(vec31);
            EntityKnockbackEvent event = CraftEventFactory.callEntityKnockbackEvent((CraftLivingEntity) entity.getBukkitEntity(), source, EntityKnockbackEvent.KnockbackCause.EXPLOSION, d13, vec31, result.x, result.y, result.z);

            // SPIGOT-7640: Need to subtract entity movement from the event result,
            // since the code below (the setDeltaMovement call as well as the hitPlayers map)
            // want the vector to be the relative velocity will the event provides the absolute velocity
            this.vec31 = (event.isCancelled()) ? Vec3.ZERO : new Vec3(event.getFinalKnockback().getX(), event.getFinalKnockback().getY(), event.getFinalKnockback().getZ()).subtract(entity.getDeltaMovement());
        }
        // CraftBukkit end
    }

    @Inject(method = "finalizeExplosion", at = @At(value = "INVOKE", target = "Lnet/minecraft/Util;shuffle(Ljava/util/List;Lnet/minecraft/util/RandomSource;)V"), cancellable = true)
    private void neotent$finalizeExplosion(CallbackInfo ci) {
        // CraftBukkit start
        org.bukkit.World bworld = this.level.getWorld();
        Location location = new Location(bworld, this.x, this.y, this.z);

        List<org.bukkit.block.Block> blockList = new ObjectArrayList<>();
        for (int i1 = this.toBlow.size() - 1; i1 >= 0; i1--) {
            BlockPos cpos = this.toBlow.get(i1);
            org.bukkit.block.Block bblock = bworld.getBlockAt(cpos.getX(), cpos.getY(), cpos.getZ());
            if (!bblock.getType().isAir()) {
                blockList.add(bblock);
            }
        }

        List<org.bukkit.block.Block> bukkitBlocks;

        if (this.source != null) {
            EntityExplodeEvent event = CraftEventFactory.callEntityExplodeEvent(this.source, blockList, this.yield, getBlockInteraction());
            this.wasCanceled = event.isCancelled();
            bukkitBlocks = event.blockList();
            this.yield = event.getYield();
        } else {
            org.bukkit.block.Block block = location.getBlock();
            org.bukkit.block.BlockState blockState = (damageSource.getDirectBlockState() != null) ? damageSource.getDirectBlockState() : block.getState();
            BlockExplodeEvent event = CraftEventFactory.callBlockExplodeEvent(block, blockState, blockList, this.yield, getBlockInteraction());
            this.wasCanceled = event.isCancelled();
            bukkitBlocks = event.blockList();
            this.yield = event.getYield();
        }

        this.toBlow.clear();

        for (org.bukkit.block.Block bblock : bukkitBlocks) {
            BlockPos coords = new BlockPos(bblock.getX(), bblock.getY(), bblock.getZ());
            toBlow.add(coords);
        }

        if (this.wasCanceled) {
            ci.cancel();
            return;
        }
        // CraftBukkit end
    }

    @Shadow
    public Explosion.BlockInteraction getBlockInteraction() {
        return this.blockInteraction;
    }

    @Inject(method = "finalizeExplosion", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;onExplosionHit(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/Explosion;Ljava/util/function/BiConsumer;)V", shift = At.Shift.BEFORE), cancellable = true)
    private void neotent$callTNTPrimeEvent(CallbackInfo ci, @Local BlockPos blockpos) {
        // CraftBukkit start - TNTPrimeEvent
        BlockState iblockdata = this.level.getBlockState(blockpos);
        Block block = iblockdata.getBlock();
        if (block instanceof net.minecraft.world.level.block.TntBlock) {
            Entity sourceEntity = source == null ? null : source;
            BlockPos sourceBlock = sourceEntity == null ? BlockPos.containing(this.x, this.y, this.z) : null;
            if (!CraftEventFactory.callTNTPrimeEvent(this.level, blockpos, org.bukkit.event.block.TNTPrimeEvent.PrimeCause.EXPLOSION, sourceEntity, sourceBlock)) {
                this.level.sendBlockUpdated(blockpos, Blocks.AIR.defaultBlockState(), iblockdata, 3); // Update the block on the client
            } else {
                ci.cancel();
            }
        }
        // CraftBukkit end
    }

    @Redirect(method = "finalizeExplosion", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private boolean neotenet$ignitionByExplosion(Level instance, BlockPos p_46598_, BlockState p_46599_, @Cancellable CallbackInfo ci) {
        // CraftBukkit start - Ignition by explosion
        if (!org.bukkit.craftbukkit.event.CraftEventFactory.callBlockIgniteEvent(instance, p_46598_, ((Explosion) (Object) this)).isCancelled()) {
            this.level.setBlockAndUpdate(p_46598_, FireBlock.getState(this.level, p_46598_));
        }
        // CraftBukkit end
        return true;
    }

    @Inject(method = "addOrAppendStack", at = @At("HEAD"), cancellable = true)
    private static void neotenet$addOrAppendStack(CallbackInfo ci, @Local(argsOnly = true) ItemStack p_312913_) {
        if (p_312913_.isEmpty()) ci.cancel();
    }
}
