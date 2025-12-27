package org.teneted.neotenet.mixin.world.entity.decoration;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.decoration.BlockAttachedEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.bukkit.entity.Hanging;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockAttachedEntity.class)
public abstract class MixinBlockAttachedEntity extends Entity {

    public MixinBlockAttachedEntity(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/decoration/BlockAttachedEntity;discard()V"), cancellable = true)
    private void neotenet$callHangingBreakEvent(CallbackInfo ci) {
        // CraftBukkit start - fire break events
        BlockState material = this.level().getBlockState(this.blockPosition());
        HangingBreakEvent.RemoveCause cause;

        if (!material.isAir()) {
            // TODO: This feels insufficient to catch 100% of suffocation cases
            cause = HangingBreakEvent.RemoveCause.OBSTRUCTION;
        } else {
            cause = HangingBreakEvent.RemoveCause.PHYSICS;
        }

        HangingBreakEvent event = new HangingBreakEvent((Hanging) this.getBukkitEntity(), cause);
        this.level().getCraftServer().getPluginManager().callEvent(event);

        if (this.isRemoved() || event.isCancelled()) {
            ci.cancel();
            return;
        }
        // CraftBukkit end
        this.pushRemoveCause(EntityRemoveEvent.Cause.DROP);
    }

    @Inject(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/decoration/BlockAttachedEntity;kill()V"), cancellable = true)
    private void neotenet$callHangingBreakEvent0(DamageSource p_345749_, float p_345893_, CallbackInfoReturnable<Boolean> cir) {
        // CraftBukkit start - fire break events
        Entity damager = (p_345749_.isDirect()) ? p_345749_.getDirectEntity() : p_345749_.getEntity();
        HangingBreakEvent event;
        if (damager != null) {
            event = new HangingBreakByEntityEvent((Hanging) this.getBukkitEntity(), damager.getBukkitEntity(), p_345749_.is(DamageTypeTags.IS_EXPLOSION) ? HangingBreakEvent.RemoveCause.EXPLOSION : HangingBreakEvent.RemoveCause.ENTITY);
        } else {
            event = new HangingBreakEvent((Hanging) this.getBukkitEntity(), p_345749_.is(DamageTypeTags.IS_EXPLOSION) ? HangingBreakEvent.RemoveCause.EXPLOSION : HangingBreakEvent.RemoveCause.DEFAULT);
        }

        this.level().getCraftServer().getPluginManager().callEvent(event);

        if (this.isRemoved() || event.isCancelled()) {
            cir.setReturnValue(true);
        }
        // CraftBukkit end
    }

    @Inject(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/decoration/BlockAttachedEntity;kill()V"), cancellable = true)
    private void neotenet$callHangingBreakEvent2(MoverType p_345778_, Vec3 p_345301_, CallbackInfo ci) {
        if (this.isRemoved()) {
            ci.cancel();
            return; // CraftBukkit
        }

        // CraftBukkit start - fire break events
        // TODO - Does this need its own cause? Seems to only be triggered by pistons
        HangingBreakEvent event = new HangingBreakEvent((Hanging) this.getBukkitEntity(), HangingBreakEvent.RemoveCause.PHYSICS);
        this.level().getCraftServer().getPluginManager().callEvent(event);

        if (this.isRemoved() || event.isCancelled()) {
            ci.cancel();
            return;
        }
        // CraftBukkit end
    }

    @ModifyExpressionValue(method = "push", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/decoration/BlockAttachedEntity;isRemoved()Z"))
    private boolean neotenet$false(boolean original) {
        return original && false;
    }

    // CraftBukkit start - selectively save tile position
    @Override
    public void addAdditionalSaveData(CompoundTag nbttagcompound, boolean includeAll) {
        if (includeAll) {
            addAdditionalSaveData(nbttagcompound);
        }
    }
    // CraftBukkit end
}
