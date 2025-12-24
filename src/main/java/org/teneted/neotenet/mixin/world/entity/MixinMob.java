package org.teneted.neotenet.mixin.world.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.EntityTransformEvent;
import org.bukkit.event.entity.EntityUnleashEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.teneted.neotenet.NeoTenet;
import org.teneted.neotenet.injection.world.entity.InjectionMob;

// Banner - TODO fix patches
@Mixin(Mob.class)
public abstract class MixinMob extends LivingEntity implements InjectionMob {

    public boolean aware = true; // CraftBukkit
    protected transient boolean neotenet$targetSuccess = false;
    @Shadow
    private boolean persistenceRequired;
    @Shadow
    @Nullable
    private LivingEntity target;
    @Shadow
    @Nullable
    private Leashable.LeashData leashData;
    private transient EntityTargetEvent.TargetReason neotenet$reason;
    private transient boolean neotenet$fireEvent;
    private transient ItemEntity neotenet$item;
    private transient EntityTransformEvent.TransformReason neotenet$transform;

    protected MixinMob(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    @Nullable
    protected abstract SoundEvent getAmbientSound();

    @Shadow
    @Nullable
    public abstract LivingEntity getTarget();

    /**
     * @author wdog5
     * @reason
     */
    @Overwrite
    public void setTarget(@Nullable LivingEntity livingEntity) {
        boolean fireEvent = neotenet$fireEvent;
        neotenet$fireEvent = false;
        EntityTargetEvent.TargetReason reason = neotenet$reason == null ? EntityTargetEvent.TargetReason.UNKNOWN : neotenet$reason;
        neotenet$reason = null;
        if (getTarget() == livingEntity) {
            neotenet$targetSuccess = false;
            return;
        }
        if (fireEvent) {
            if (reason == EntityTargetEvent.TargetReason.UNKNOWN && this.getTarget() != null && livingEntity == null) {
                reason = (this.getTarget().isAlive() ? EntityTargetEvent.TargetReason.FORGOT_TARGET : EntityTargetEvent.TargetReason.TARGET_DIED);
            }
            if (reason == EntityTargetEvent.TargetReason.UNKNOWN) {
                NeoTenet.LOGGER.warn("Unknown target reason setting {} target to {}", this, livingEntity);
            }
            CraftLivingEntity ctarget = null;
            if (livingEntity != null) {
                ctarget = (CraftLivingEntity) livingEntity.getBukkitEntity();
            }
            EntityTargetLivingEntityEvent event = new EntityTargetLivingEntityEvent(this.getBukkitEntity(), ctarget, reason);
            Bukkit.getPluginManager().callEvent(event);
            level().getCraftServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                neotenet$targetSuccess = false;
                return;
            }
            if (event.getTarget() != null) {
                livingEntity = ((CraftLivingEntity) event.getTarget()).getHandle();
            } else {
                livingEntity = null;
            }
        }
        this.target = livingEntity;
        neotenet$targetSuccess = true;
    }

    @Shadow
    protected abstract boolean canReplaceCurrentItem(ItemStack candidate, ItemStack existing);

    @Shadow
    public abstract boolean canHoldItem(ItemStack stack);

    @Shadow
    protected abstract float getEquipmentDropChance(EquipmentSlot slot);

    @Shadow
    protected abstract void setItemSlotAndDropWhenKilled(EquipmentSlot slot, ItemStack stack);

    @Shadow
    @Nullable
    public abstract <T extends Mob> T convertTo(EntityType<T> entityType, boolean bl);

    @Shadow
    private boolean canPickUpLoot;

    @Inject(method = "setCanPickUpLoot", at = @At("HEAD"))
    public void neotenet$setPickupLoot(boolean canPickup, CallbackInfo ci) {
        this.canPickUpLoot = canPickup;
    }

    /**
     * @author wdog5
     * @reason
     */
    @Overwrite
    public boolean canPickUpLoot() {
        return super.bukkitPickUpLoot;
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void neotenet$init(EntityType<? extends Mob> type, Level worldIn, CallbackInfo ci) {
        this.aware = true;
    }

    @Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
    private void neotenet$setAware(CompoundTag compound, CallbackInfo ci) {
        compound.putBoolean("Bukkit.Aware", this.aware);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
    private void neotenet$readAware(CompoundTag compound, CallbackInfo ci) {
        if (compound.contains("Bukkit.Aware")) {
            this.aware = compound.getBoolean("Bukkit.Aware");
        }
    }

    @Redirect(method = "readAdditionalSaveData", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;setCanPickUpLoot(Z)V"))
    public void neotenet$setIfTrue(Mob mobEntity, boolean canPickup) {
        if (canPickup) mobEntity.setCanPickUpLoot(true);
    }

    @Inject(method = "serverAiStep", cancellable = true, at = @At("HEAD"))
    private void neotenet$unaware(CallbackInfo ci) {
        if (!this.aware) {
            ++this.noActionTime;
            ci.cancel();
        }
    }

    @Inject(method = "pickUpItem", at = @At("HEAD"))
    private void neotenet$captureItemEntity(ItemEntity itemEntity, CallbackInfo ci) {
        neotenet$item = itemEntity;
    }

    /**
     * @author wdog5
     * @reason
     */
    @Overwrite
    public ItemStack equipItemIfPossible(ItemStack stack) {
        ItemEntity itemEntity = neotenet$item;
        neotenet$item = null;
        EquipmentSlot equipmentslottype = getEquipmentSlotForItem(stack);
        ItemStack itemstack = this.getItemBySlot(equipmentslottype);
        boolean flag = this.canReplaceCurrentItem(stack, itemstack);

        if (equipmentslottype.isArmor() && !flag) {
            equipmentslottype = EquipmentSlot.MAINHAND;
            itemstack = this.getItemBySlot(equipmentslottype);
            flag = itemstack.isEmpty();
        }

        boolean canPickup = flag && this.canHoldItem(stack);
        if (itemEntity != null) {
            canPickup = !CraftEventFactory.callEntityPickupItemEvent((Mob) (Object) this, itemEntity, 0, !canPickup).isCancelled();
        }
        if (canPickup) {
            double d0 = this.getEquipmentDropChance(equipmentslottype);
            if (!itemstack.isEmpty() && (double) Math.max(this.random.nextFloat() - 0.1F, 0.0F) < d0) {
                this.forceDrops = true;
                this.spawnAtLocation(itemstack);
                this.forceDrops = false;
            }

            if (equipmentslottype.isArmor() && stack.getCount() > 1) {
                ItemStack itemstack1 = stack.copyWithCount(1);
                this.setItemSlotAndDropWhenKilled(equipmentslottype, itemstack1);
                return itemstack1;
            } else {
                this.setItemSlotAndDropWhenKilled(equipmentslottype, stack);
                return stack;
            }
        } else {
            return ItemStack.EMPTY;
        }
    }

    // Banner TODO fixme
    @Inject(method = "interact", cancellable = true, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;checkAndHandleImportantInteractions(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;"))
    private void neotenet$unleash(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (CraftEventFactory.callPlayerUnleashEntityEvent((Mob) (Object) this, player, hand).isCancelled() && this.leashData != null) {
            ((ServerPlayer) player).connection.send(new ClientboundSetEntityLinkPacket((Mob) (Object) this, this.leashData.leashHolder));
            cir.setReturnValue(InteractionResult.PASS);
        }
    }

    @Inject(method = "startRiding", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;dropLeash(ZZ)V"))
    private void neotenet$unleashRide(Entity entityIn, boolean force, CallbackInfoReturnable<Boolean> cir) {
        Bukkit.getPluginManager().callEvent(new EntityUnleashEvent(this.getBukkitEntity(), EntityUnleashEvent.UnleashReason.UNKNOWN));
    }

    @Inject(method = "convertTo", at = @At("RETURN"))
    private <T extends Mob> void neotenet$cleanReason(EntityType<T> p_233656_1_, boolean p_233656_2_, CallbackInfoReturnable<T> cir) {
        this.level().pushAddEntityReason(null);
        this.neotenet$transform = null;
    }

    @Override
    public <T extends Mob> T convertTo(EntityType<T> entitytypes, boolean flag, EntityTransformEvent.TransformReason transformReason, CreatureSpawnEvent.SpawnReason spawnReason) {
        this.level().pushAddEntityReason(spawnReason);
        bridge$pushTransformReason(transformReason);
        return this.convertTo(entitytypes, flag);
    }

    @Override
    public void bridge$pushTransformReason(EntityTransformEvent.TransformReason transformReason) {
        this.neotenet$transform = transformReason;
    }

    @Override
    public boolean setTarget(LivingEntity entityliving, EntityTargetEvent.TargetReason reason, boolean fireEvent) {
        bridge$pushGoalTargetReason(reason, fireEvent);
        setTarget(entityliving);
        return neotenet$targetSuccess;
    }

    @Override
    public void bridge$pushGoalTargetReason(EntityTargetEvent.TargetReason reason, boolean fireEvent) {
        if (fireEvent) {
            this.neotenet$reason = reason;
        } else {
            this.neotenet$reason = null;
        }
        neotenet$fireEvent = fireEvent;
    }

    @Override
    public SoundEvent getAmbientSound0() {
        return getAmbientSound();
    }

    @Override
    public void setPersistenceRequired(boolean persistenceRequired) {
        this.persistenceRequired = persistenceRequired;
    }
}