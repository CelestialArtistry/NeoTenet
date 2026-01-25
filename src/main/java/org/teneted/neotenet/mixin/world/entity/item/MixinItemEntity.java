package org.teneted.neotenet.mixin.world.entity.item;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemEntity.class)
public abstract class MixinItemEntity extends Entity {

    public MixinItemEntity(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;discard()V"))
    private void neotenet$discardCause(CallbackInfo ci) {
        this.pushRemoveCause(EntityRemoveEvent.Cause.DESPAWN);
    }

    @Inject(method = "merge(Lnet/minecraft/world/entity/item/ItemEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/item/ItemEntity;Lnet/minecraft/world/item/ItemStack;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;discard()V"))
    private static void neotenet$discardCause0(CallbackInfo ci, @Local(ordinal = 1, argsOnly = true) ItemEntity itemEntity) {
        itemEntity.pushRemoveCause(EntityRemoveEvent.Cause.MERGE);
    }

    @Inject(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;discard()V"))
    private void neotenet$discardCause1(DamageSource p_32013_, float p_32014_, CallbackInfoReturnable<Boolean> cir) {
        this.pushRemoveCause(EntityRemoveEvent.Cause.DEATH);
    }

    @Inject(method = "readAdditionalSaveData", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;discard()V"))
    private void neotenet$discardCause2(CompoundTag p_32034_, CallbackInfo ci) {
        this.pushRemoveCause(null);
    }

    @Inject(method = "merge(Lnet/minecraft/world/entity/item/ItemEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/item/ItemEntity;Lnet/minecraft/world/item/ItemStack;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;merge(Lnet/minecraft/world/entity/item/ItemEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)V"), cancellable = true)
    private static void neotenet$callItemMergeEvent(ItemEntity p_32018_, ItemStack p_32019_, ItemEntity p_32020_, ItemStack p_32021_, CallbackInfo ci) {
        // CraftBukkit start
        if (!CraftEventFactory.callItemMergeEvent(p_32020_, p_32018_)) {
            ci.cancel();
            return;
        }
        // CraftBukkit end
    }

    @Inject(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;markHurt()V"), cancellable = true)
    private void neotenet$handleNonLivingEntityDamageEvent(DamageSource p_32013_, float p_32014_, CallbackInfoReturnable<Boolean> cir) {
        // CraftBukkit start
        if (CraftEventFactory.handleNonLivingEntityDamageEvent(this, p_32013_, p_32014_)) {
            cir.setReturnValue(false);
        }
        // CraftBukkit end
    }

    @Inject(method = "playerTouch", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;discard()V"))
    private void neotenet$discardCause3(Player p_32040_, CallbackInfo ci) {
        this.pushRemoveCause(EntityRemoveEvent.Cause.PICKUP);
    }
}
