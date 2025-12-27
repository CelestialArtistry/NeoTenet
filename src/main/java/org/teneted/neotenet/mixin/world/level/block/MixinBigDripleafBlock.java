package org.teneted.neotenet.mixin.world.level.block;

import com.llamalad7.mixinextras.sugar.Cancellable;
import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BigDripleafBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Tilt;
import net.minecraft.world.phys.BlockHitResult;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityInteractEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Mixin(BigDripleafBlock.class)
public abstract class MixinBigDripleafBlock {

    @Shadow
    @Final
    private static EnumProperty<Tilt> TILT;

    @Shadow
    protected static void setTilt(BlockState p_152278_, Level p_152279_, BlockPos p_152280_, Tilt p_152281_) {
    }

    @Shadow
    protected static void playTiltSound(Level p_152233_, BlockPos p_152234_, net.minecraft.sounds.SoundEvent p_152235_) {
    }

    @Shadow
    @Final
    private static Object2IntMap<Tilt> DELAY_UNTIL_NEXT_TILT_STATE;
    @Unique
    private static AtomicReference<Entity> neotenet$entity = new AtomicReference<>(null);
    @Unique
    private static AtomicBoolean neotenet$tiltBoolean = new AtomicBoolean(false);

    @Inject(method = "resetTilt",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/BigDripleafBlock;setTilt(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/properties/Tilt;)V"))
    private static void neotenet$setNullEntity(CallbackInfo ci) {
        neotenet$entity.getAndSet(null);
    }

    @Inject(method = "setTilt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getValue(Lnet/minecraft/world/level/block/state/properties/Property;)Ljava/lang/Comparable;"), cancellable = true)
    private static void neotenet$callEntityChangeBlockEvent(BlockState p_152278_, Level p_152279_, BlockPos p_152280_, Tilt p_152281_, CallbackInfo ci) {
        if (neotenet$entity.get() != null) {
            if (!CraftEventFactory.callEntityChangeBlockEvent(neotenet$entity.get(), p_152280_, p_152278_.setValue(TILT, p_152281_))) {
                neotenet$tiltBoolean.set(false);
                ci.cancel();
            }
        }
        neotenet$tiltBoolean.getAndSet(true);
        // CraftBukkit end
    }

    @Inject(method = "entityInside", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/BigDripleafBlock;setTiltAndScheduleTick(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/properties/Tilt;Lnet/minecraft/sounds/SoundEvent;)V"))
    private void neotenet$callPlayerInteractEvent(BlockState p_152266_, Level p_152267_, BlockPos p_152268_, Entity p_152269_, CallbackInfo ci) {
        // CraftBukkit start - tilt dripleaf
        org.bukkit.event.Cancellable cancellable;
        if (p_152269_ instanceof Player) {
            cancellable = CraftEventFactory.callPlayerInteractEvent((Player) p_152269_, org.bukkit.event.block.Action.PHYSICAL, p_152268_, null, null, null);
        } else {
            cancellable = new EntityInteractEvent(p_152269_.getBukkitEntity(), p_152267_.getWorld().getBlockAt(p_152268_.getX(), p_152268_.getY(), p_152268_.getZ()));
            p_152267_.getCraftServer().getPluginManager().callEvent((EntityInteractEvent) cancellable);
        }

        if (cancellable.isCancelled()) {
            ci.cancel();
            return;
        }
        // CraftBukkit end
    }

    @Inject(method = "onProjectileHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/BigDripleafBlock;setTiltAndScheduleTick(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/properties/Tilt;Lnet/minecraft/sounds/SoundEvent;)V"))
    private void neotenet$setProjectile(Level p_152228_, BlockState p_152229_, BlockHitResult p_152230_, Projectile p_152231_, CallbackInfo ci) {
        neotenet$entity.getAndSet(p_152231_);
    }

    @Inject(method = "entityInside", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/BigDripleafBlock;setTiltAndScheduleTick(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/properties/Tilt;Lnet/minecraft/sounds/SoundEvent;)V"))
    private void neotenet$setEntity(BlockState p_152266_, Level p_152267_, BlockPos p_152268_, Entity p_152269_, CallbackInfo ci) {
        neotenet$entity.getAndSet(null);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/BigDripleafBlock;setTiltAndScheduleTick(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/properties/Tilt;Lnet/minecraft/sounds/SoundEvent;)V"))
    private void neotenet$setNullEntity0(CallbackInfo ci) {
        neotenet$entity.getAndSet(null);
    }

    @Inject(method = "setTiltAndScheduleTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/BigDripleafBlock;setTilt(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/properties/Tilt;)V"), cancellable = true)
    private void neotenet$setTiltAndScheduleTick(BlockState p_152283_, Level p_152284_, BlockPos p_152285_, Tilt p_152286_, SoundEvent p_152287_, CallbackInfo ci) {
        if (!neotenet$tiltBoolean.get()) {
            ci.cancel();
        }
    }

    private void setTiltAndScheduleTick(BlockState iblockdata, Level world, BlockPos blockposition, Tilt tilt, @Nullable SoundEvent soundeffect, @Nullable Entity entity) {
        if (!setTilt(iblockdata, world, blockposition, tilt, entity)) return;
        // CraftBukkit end
        if (soundeffect != null) {
            playTiltSound(world, blockposition, soundeffect);
        }

        int i = DELAY_UNTIL_NEXT_TILT_STATE.getInt(entity);
        if (i != -1) {
            world.scheduleTick(blockposition, ((BigDripleafBlock) (Object) this), i);
        }
    }

    private static boolean setTilt (BlockState iblockdata, Level world, BlockPos blockposition, Tilt tilt, @Nullable Entity entity){
        neotenet$entity.getAndSet(entity);
        setTilt(iblockdata, world, blockposition, tilt);
        return neotenet$tiltBoolean.getAndSet(true);
    }
}
