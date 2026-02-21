package org.teneted.neotenet.mixin.world.entity.projectile;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.bukkit.entity.FishHook;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import javax.annotation.Nullable;

@Mixin(FishingHook.class)
public abstract class MixinFishingHook  extends Projectile {

    @Shadow
    public boolean rainInfluenced;

    @Shadow
    public boolean skyInfluenced;

    @Shadow
    @Nullable
    public abstract Player getPlayerOwner();

    @Shadow
    public float minLureAngle;

    @Shadow
    public float maxLureAngle;

    @Shadow
    public int minLureTime;

    @Shadow
    public int maxLureTime;

    @Shadow
    public int minWaitTime;

    @Shadow
    public int maxWaitTime;

    @Shadow
    private int timeUntilLured;

    @Shadow
    public boolean applyLure;

    @Shadow
    @Final
    private int lureSpeed;

    @Shadow
    @Nullable
    private Entity hookedIn;

    protected MixinFishingHook(EntityType<? extends Projectile> p_37248_, Level p_37249_) {
        super(p_37248_, p_37249_);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/FishingHook;discard()V"))
    private void neotenet$pushDiscardReason(CallbackInfo ci) {
        this.pushRemoveCause(EntityRemoveEvent.Cause.DESPAWN);
    }

    @Inject(method = "shouldStopFishing", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/FishingHook;discard()V"))
    private void neotenet$pushDiscardReason0(Player p_37137_, CallbackInfoReturnable<Boolean> cir) {
        this.pushRemoveCause(EntityRemoveEvent.Cause.DESPAWN);
    }

    @Redirect(method = "checkCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/FishingHook;onHit(Lnet/minecraft/world/phys/HitResult;)V"))
    private void neotenet$usePreHit(FishingHook instance, HitResult hitResult) {
        this.preHitTargetOrDeflectSelf(hitResult); // CraftBukkit - projectile hit event
    }

    @ModifyExpressionValue(method = "catchingFish", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isRainingAt(Lnet/minecraft/core/BlockPos;)Z"))
    private boolean neotenet$checkRain(boolean original) {
        return this.rainInfluenced && original;
    }

    @ModifyExpressionValue(method = "catchingFish", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;canSeeSky(Lnet/minecraft/core/BlockPos;)Z"))
    private boolean neotenet$checkCanSeeSky(boolean original) {
        return this.skyInfluenced && original;
    }

    @Inject(method = "catchingFish", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/syncher/SynchedEntityData;set(Lnet/minecraft/network/syncher/EntityDataAccessor;Ljava/lang/Object;)V", shift = At.Shift.AFTER))
    private void neotenet$callPlayerFishEvent(BlockPos p_37146_, CallbackInfo ci) {
        // CraftBukkit start
        PlayerFishEvent playerFishEvent = new PlayerFishEvent((org.bukkit.entity.Player) this.getPlayerOwner().getBukkitEntity(), null, (FishHook) this.getBukkitEntity(), PlayerFishEvent.State.FAILED_ATTEMPT);
        this.level().getCraftServer().getPluginManager().callEvent(playerFishEvent);
        // CraftBukkit end
    }

    @Inject(method = "catchingFish", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/FishingHook;playSound(Lnet/minecraft/sounds/SoundEvent;FF)V"), cancellable = true)
    private void neotenet$callPlayerFishEvent0(BlockPos p_37146_, CallbackInfo ci) {
        // CraftBukkit start
        PlayerFishEvent playerFishEvent = new PlayerFishEvent((org.bukkit.entity.Player) this.getPlayerOwner().getBukkitEntity(), null, (FishHook) this.getBukkitEntity(), PlayerFishEvent.State.BITE);
        this.level().getCraftServer().getPluginManager().callEvent(playerFishEvent);
        if (playerFishEvent.isCancelled()) {
            ci.cancel();
            return;
        }
        // CraftBukkit end
    }

    @ModifyArgs(method = "catchingFish", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;nextFloat(Lnet/minecraft/util/RandomSource;FF)F", ordinal = 2))
    private void neotenet$modifyFishing(Args args) {
        args.set(1, this.minLureAngle);
        args.set(2, this.maxLureAngle);
    }

    @ModifyArgs(method = "catchingFish", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;nextInt(Lnet/minecraft/util/RandomSource;II)I", ordinal = 0))
    private void neotenet$modifyFishing0(Args args) {
        args.set(1, this.minLureTime);
        args.set(2, this.maxLureTime);
    }

    @ModifyArgs(method = "catchingFish", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;nextInt(Lnet/minecraft/util/RandomSource;II)I", ordinal = 1))
    private void neotenet$modifyFishing1(Args args) {
        args.set(1, this.minWaitTime);
        args.set(2, this.maxWaitTime);
    }

    @Redirect(method = "catchingFish", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/projectile/FishingHook;timeUntilLured:I", ordinal = 11))
    private void neotenet$addCheckLure(FishingHook instance, int value) {
        this.timeUntilLured -= (this.applyLure) ? this.lureSpeed : 0;
    }

    @Inject(method = "retrieve", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/FishingHook;pullEntity(Lnet/minecraft/world/entity/Entity;)V"), cancellable = true)
    private void neotenet$callPlayerFishEvent1(ItemStack p_37157_, CallbackInfoReturnable<Integer> cir, @Local Player player) {
        // CraftBukkit start
        PlayerFishEvent playerFishEvent = new PlayerFishEvent((org.bukkit.entity.Player) player.getBukkitEntity(), this.hookedIn.getBukkitEntity(), (FishHook) this.getBukkitEntity(), PlayerFishEvent.State.CAUGHT_ENTITY);
        this.level().getCraftServer().getPluginManager().callEvent(playerFishEvent);

        if (playerFishEvent.isCancelled()) {
            cir.setReturnValue(0);
        }
        // CraftBukkit end
    }

    @Inject(method = "retrieve", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;setDeltaMovement(DDD)V", shift = At.Shift.AFTER), cancellable = true)
    private void neotenet$callPlayerFishEvent2(ItemStack p_37157_, CallbackInfoReturnable<Integer> cir, @Local Player player, @Local ItemEntity itementity, @Share("bukkitEvent") LocalRef<PlayerFishEvent> bukkitEvent) {
        // CraftBukkit start
        PlayerFishEvent playerFishEvent = new PlayerFishEvent((org.bukkit.entity.Player) player.getBukkitEntity(), itementity.getBukkitEntity(), (FishHook) this.getBukkitEntity(), PlayerFishEvent.State.CAUGHT_FISH);
        playerFishEvent.setExpToDrop(this.random.nextInt(6) + 1);
        this.level().getCraftServer().getPluginManager().callEvent(playerFishEvent);
        bukkitEvent.set(playerFishEvent);

        if (playerFishEvent.isCancelled()) {
            cir.setReturnValue(0);
        }
        // CraftBukkit end
    }

    @WrapOperation(method = "retrieve", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z", ordinal = 1))
    private boolean neotenet$checkExp(Level instance, Entity entity, Operation<Boolean> original, @Share("bukkitEvent") LocalRef<PlayerFishEvent> bukkitEvent) {
        if (bukkitEvent.get().getExpToDrop() > 0) {
            return original.call(instance, entity);
        } else {
            return false;
        }
    }

    @Inject(method = "retrieve", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/FishingHook;onGround()Z", ordinal = 1, shift = At.Shift.AFTER), cancellable = true)
    private void neotenet$callPlayerFishEvent(ItemStack p_37157_, CallbackInfoReturnable<Integer> cir) {
        // CraftBukkit start
        PlayerFishEvent playerFishEvent = new PlayerFishEvent((org.bukkit.entity.Player) this.getPlayerOwner().getBukkitEntity(), null, (FishHook) this.getBukkitEntity(), PlayerFishEvent.State.IN_GROUND);
        this.level().getCraftServer().getPluginManager().callEvent(playerFishEvent);

        if (playerFishEvent.isCancelled()) {
            cir.setReturnValue(0);
        }
        // CraftBukkit end
    }

    @Inject(method = "retrieve", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/FishingHook;discard()V"), cancellable = true)
    private void neotenet$callPlayerFishEventAndPushCause(ItemStack p_37157_, CallbackInfoReturnable<Integer> cir, @Local int i) {
        // CraftBukkit start
        if (i == 0) {
            PlayerFishEvent playerFishEvent = new PlayerFishEvent((org.bukkit.entity.Player) this.getPlayerOwner().getBukkitEntity(), null, (FishHook) this.getBukkitEntity(), PlayerFishEvent.State.REEL_IN);
            this.level().getCraftServer().getPluginManager().callEvent(playerFishEvent);
            if (playerFishEvent.isCancelled()) {
                cir.setReturnValue(0);
            }
        }
        // CraftBukkit end
        this.pushRemoveCause(EntityRemoveEvent.Cause.DESPAWN);
    }

    @Inject(method = "remove", at = @At("HEAD"))
    private void neotenet$pushRemoveCause(RemovalReason p_150146_, CallbackInfo ci) {
        this.pushRemoveCause(null);
    }
}
