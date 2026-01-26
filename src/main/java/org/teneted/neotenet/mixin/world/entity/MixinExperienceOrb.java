package org.teneted.neotenet.mixin.world.entity;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.level.Level;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerExpCooldownChangeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(ExperienceOrb.class)
public abstract class MixinExperienceOrb extends Entity {

    @Shadow
    private Player followingPlayer;

    @Shadow
    private int value;

    public MixinExperienceOrb(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isSpectator()Z"))
    private boolean neotenet$callEntityTargetLivingEvent(boolean original) {
        Player prevTarget = this.followingPlayer;// CraftBukkit - store old target
        // CraftBukkit start
        boolean cancelled = false;
        if (this.followingPlayer != prevTarget) {
            EntityTargetLivingEntityEvent event = CraftEventFactory.callEntityTargetLivingEvent(this, followingPlayer, (followingPlayer != null) ? EntityTargetEvent.TargetReason.CLOSEST_PLAYER : EntityTargetEvent.TargetReason.FORGOT_TARGET);
            LivingEntity target = (event.getTarget() == null) ? null : ((org.bukkit.craftbukkit.entity.CraftLivingEntity) event.getTarget()).getHandle();
            cancelled = event.isCancelled();

            if (cancelled) {
                followingPlayer = prevTarget;
            } else {
                followingPlayer = (target instanceof Player) ? (Player) target : null;
            }
        }
        return original && !cancelled;
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ExperienceOrb;discard()V"))
    private void neotenet$discardCause(CallbackInfo ci) {
        this.pushRemoveCause(EntityRemoveEvent.Cause.DESPAWN);
    }

    @Inject(method = "merge", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ExperienceOrb;discard()V"))
    private void neotenet$discardCause0(CallbackInfo ci) {
        this.pushRemoveCause(EntityRemoveEvent.Cause.MERGE);
    }

    @Inject(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ExperienceOrb;discard()V"))
    private void neotenet$discardCause1(DamageSource p_20785_, float p_20786_, CallbackInfoReturnable<Boolean> cir) {
        this.pushRemoveCause(EntityRemoveEvent.Cause.DEATH);
    }

    @Inject(method = "playerTouch", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ExperienceOrb;discard()V"))
    private void neotenet$discardCause2(Player p_20792_, CallbackInfo ci) {
        this.pushRemoveCause(EntityRemoveEvent.Cause.PICKUP);
    }

    @Redirect(method = "playerTouch", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/player/Player;takeXpDelay:I", ordinal = 1))
    private void neotenet$callPlayerXpCooldownEvent(Player instance, int value) {
        instance.takeXpDelay = CraftEventFactory.callPlayerXpCooldownEvent(instance, 2, PlayerExpCooldownChangeEvent.ChangeReason.PICKUP_ORB).getNewCooldown(); // CraftBukkit - entityhuman.takeXpDelay = 2;
    }

    @ModifyArg(method = "playerTouch", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;giveExperiencePoints(I)V"), index = 0)
    private int neotenet$callPlayerExpChangeEvent(int p_36291_, @Local(argsOnly = true) Player p_20792_) {
        return CraftEventFactory.callPlayerExpChangeEvent(p_20792_, p_36291_).getAmount();
    }

    @Inject(method = "repairPlayerItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;setDamageValue(I)V"), cancellable = true)
    private void neotenet$callPlayerItemMendEvent(ServerPlayer p_344821_, int p_147094_, CallbackInfoReturnable<Integer> cir, @Local(ordinal = 2) int k, @Local Optional<EnchantedItemInUse> optional, @Local ItemStack itemstack) {
        // CraftBukkit start
        org.bukkit.event.player.PlayerItemMendEvent event = CraftEventFactory.callPlayerItemMendEvent(p_344821_, ((ExperienceOrb) (Object) this), itemstack, optional.get().inSlot(), k);
        k = event.getRepairAmount();
        if (event.isCancelled()) {
            cir.setReturnValue(p_147094_);
        }
        // CraftBukkit end
    }

    @Inject(method = "repairPlayerItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ExperienceOrb;repairPlayerItems(Lnet/minecraft/server/level/ServerPlayer;I)I"))
    private void neotenet$resetValue(ServerPlayer p_344821_, int p_147094_, CallbackInfoReturnable<Integer> cir, @Local(ordinal = 3) int l) {
        this.value = l; // CraftBukkit - update exp value of orb for PlayerItemMendEvent calls
    }

    @Inject(method = "getExperienceValue", at = @At("RETURN"), cancellable = true)
    private static void neotenet$setValueExp(int i, CallbackInfoReturnable<Integer> cir) {
        // CraftBukkit start
        if (i > 162670129) cir.setReturnValue(i - 100000);
        if (i > 81335063) cir.setReturnValue(81335063);
        if (i > 40667527) cir.setReturnValue(40667527);
        if (i > 20333759) cir.setReturnValue(20333759);
        if (i > 10166857) cir.setReturnValue(10166857);
        if (i > 5083423) cir.setReturnValue(5083423);
        if (i > 2541701) cir.setReturnValue(2541701);
        if (i > 1270849) cir.setReturnValue(1270849);
        if (i > 635413) cir.setReturnValue(635413);
        if (i > 317701) cir.setReturnValue(317701);
        if (i > 158849) cir.setReturnValue(158849);
        if (i > 79423) cir.setReturnValue(79423);
        if (i > 39709) cir.setReturnValue(39709);
        if (i > 19853) cir.setReturnValue(19853);
        if (i > 9923) cir.setReturnValue(9923);
        if (i > 4957) cir.setReturnValue(4957);
        // CraftBukkit end
    }
    @Definition(id = "takeXpDelay", field = "Lnet/minecraft/world/entity/player/Player;takeXpDelay:I")
    @Definition(id = "p_20792_", local = @Local(type = Player.class, argsOnly = true))
    @Expression(value = "p_20792_.takeXpDelay == 0")
    @ModifyExpressionValue(method = "playerTouch", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean neotenet$callPlayerPickupExperienceEvent(boolean original, @Local(ordinal = 0) ServerPlayer serverplayer) {
        return original && new com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent(serverplayer.getBukkitEntity(), (org.bukkit.entity.ExperienceOrb) this.getBukkitEntity()).callEvent();
    }
}
