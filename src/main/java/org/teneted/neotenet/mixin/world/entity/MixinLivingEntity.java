package org.teneted.neotenet.mixin.world.entity;

import com.llamalad7.mixinextras.sugar.Local;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.CombatTracker;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Attackable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.attribute.CraftAttributeMap;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.teneted.neotenet.injection.world.entity.InjectionLivingEntity;

@Mixin(value = LivingEntity.class, priority = 199)
public abstract class MixinLivingEntity extends Entity implements Attackable, InjectionLivingEntity {

    @Shadow
    @Final
    public static EntityDataAccessor<Float> DATA_HEALTH_ID;
    @Shadow
    @Final
    public static EntityDataAccessor<Integer> DATA_ARROW_COUNT_ID;
    @Shadow
    @Final
    private static Logger LOGGER;

    public EntityPotionEffectEvent.Cause cause;
    @Shadow
    protected int lastHurtByPlayerTime;
    @Shadow
    protected ItemStack useItem;
    @Shadow
    @Final
    private AttributeMap attributes;
    private final AtomicReference<BlockState> neotenet$FallState = new AtomicReference<>();
    private final AtomicBoolean neotenet$silent = new AtomicBoolean(false);
    private transient EntityPotionEffectEvent.Cause neotenet$cause;
    private transient boolean neotenet$damageResult;
    private transient EntityRegainHealthEvent.RegainReason neotenet$regainReason;

    public MixinLivingEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    public abstract SoundEvent getEatingSound(ItemStack stack);

    @Shadow
    protected abstract SoundEvent getDrinkingSound(ItemStack stack);

    @Shadow
    protected abstract SoundEvent getFallDamageSound(int height);

    @Shadow
    @Nullable
    protected abstract SoundEvent getDeathSound();

    @Shadow
    public abstract void onEquipItem(EquipmentSlot equipmentSlot, ItemStack itemStack, ItemStack itemStack2);
    @Shadow
    public abstract boolean wasExperienceConsumed();

    @Shadow
    protected abstract boolean isAlwaysExperienceDropper();

    @Shadow
    public abstract boolean shouldDropExperience();

    @Shadow
    public abstract boolean removeAllEffects();

    @Shadow
    public abstract ItemStack getItemBySlot(EquipmentSlot slot);

    @Shadow
    public abstract float getHealth();

    @Shadow
    public abstract void setHealth(float health);

    @Shadow
    public abstract CombatTracker getCombatTracker();

    @Shadow
    public abstract void heal(float healAmount);

    @Shadow
    public abstract ItemStack getItemInHand(InteractionHand hand);

    @Shadow
    public abstract InteractionHand getUsedItemHand();

    @Shadow
    public abstract int getArrowCount();

    @Shadow
    protected abstract boolean doesEmitEquipEvent(EquipmentSlot slot);
    @Shadow
    public abstract boolean isSleeping();

    @Shadow
    protected abstract void actuallyHurt(DamageSource damageSource, float f);

    @Shadow
    public abstract void die(DamageSource damageSource);
    @Shadow
    public abstract boolean isDeadOrDying();

    @Shadow
    protected abstract float getSoundVolume();

    @Shadow
    public abstract float getVoicePitch();

    @Shadow
    public abstract void knockback(double d, double e, double f);

    @Shadow
    public abstract void setLastHurtByMob(@Nullable LivingEntity livingEntity);

    @Shadow
    public abstract double getAttributeValue(Holder<Attribute> holder);

    @Shadow
    @Nullable
    public abstract AttributeInstance getAttribute(Holder<Attribute> holder);

    @Shadow
    public abstract boolean hasEffect(Holder<MobEffect> holder);

    @Shadow
    public abstract int getExperienceReward(ServerLevel serverLevel, @Nullable Entity entity);

    @Shadow
    public boolean collides;

    @Mutable
    @Shadow
    @Final
    public CraftAttributeMap craftAttributes;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void neotenet$init(EntityType<? extends LivingEntity> type, Level worldIn, CallbackInfo ci) {
        this.collides = true;
        this.craftAttributes = new CraftAttributeMap(this.attributes);
        this.entityData.set(DATA_HEALTH_ID, (float) this.getAttributeValue(Attributes.MAX_HEALTH));
    }

    @Inject(method = "checkFallDamage", at = @At("HEAD"))
    private void neotenet$getFallInfo(double y, boolean onGround, BlockState state, BlockPos pos, CallbackInfo ci) {
        this.neotenet$FallState.set(state);
    }

    @Redirect(method = "checkFallDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I"))
    private <T extends ParticleOptions> int neotenet$addCheckFall(ServerLevel instance, T particleOptions, double d, double e, double f, int i, double g, double h, double j, double k) {
        // CraftBukkit start - visiblity api
        float neotenet$f = (float) Mth.ceil(this.fallDistance - 3.0F);
        double neotenet$d = Math.min(0.2F + neotenet$f / 15.0F, 2.5);
        int neotenet$i = (int) (150.0 * neotenet$d);
        if (((LivingEntity) (Object) this) instanceof ServerPlayer) {
            return ((ServerLevel) this.level()).sendParticles((ServerPlayer) (Object) this, new BlockParticleOption(ParticleTypes.BLOCK, neotenet$FallState.get()), this.getX(), this.getY(), this.getZ(), neotenet$i, 0.0D, 0.0D, 0.0D, 0.15000000596046448D, false);
        } else {
            return ((ServerLevel) this.level()).sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, neotenet$FallState.get()), this.getX(), this.getY(), this.getZ(), neotenet$i, 0.0D, 0.0D, 0.0D, 0.15000000596046448D);
        }
    }

    @Redirect(method = "onEquipItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isClientSide()Z"))
    private boolean neotenet$addSilentCheck(Level instance) {
        return !this.level().isClientSide() && !this.isSilent() && !neotenet$silent.getAndSet(false);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
    public void neotenet$readMaxHealth(CompoundTag compound, CallbackInfo ci) {
        if (compound.contains("Bukkit.MaxHealth")) {
            Tag nbtbase = compound.get("Bukkit.MaxHealth");
            if (nbtbase.getId() == 5) {
                this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(((FloatTag) nbtbase).getAsDouble());
            } else if (nbtbase.getId() == 3) {
                this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(((IntTag) nbtbase).getAsDouble());
            }
        }
    }

    @Override
    public int getExpReward(@Nullable Entity entity) {
        if (this.level() instanceof ServerLevel serverLevel && !this.wasExperienceConsumed() && (this.isAlwaysExperienceDropper() || this.lastHurtByPlayerTime > 0 && this.shouldDropExperience() && this.level().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT))) {
            int exp = this.getExperienceReward(serverLevel, entity);
            return exp;
        } else {
            return 0;
        }
    }

    /**
     * @author wdog5
     * @reason
     */
    @Overwrite
    public boolean isAlive() {
        return !this.isRemoved() && this.entityData.get(DATA_HEALTH_ID) > 0.0F;
    }

    @Override
    public void heal(float healAmount, EntityRegainHealthEvent.RegainReason regainReason) {
        pushHealReason(regainReason);
        this.heal(healAmount);
    }

    @Override
    public void pushHealReason(EntityRegainHealthEvent.RegainReason reason) {
        neotenet$regainReason = reason;
    }

    @Redirect(method = "heal", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setHealth(F)V"))
    public void neotenet$healEvent(LivingEntity livingEntity, float health) {
        EntityRegainHealthEvent.RegainReason regainReason = neotenet$regainReason == null ? EntityRegainHealthEvent.RegainReason.CUSTOM : neotenet$regainReason;
        neotenet$regainReason = null;
        float f = this.getHealth();
        float amount = health - f;
        EntityRegainHealthEvent event = new EntityRegainHealthEvent(this.getBukkitEntity(), amount, regainReason);
        if (this.valid) {
            Bukkit.getPluginManager().callEvent(event);
        }

        if (!event.isCancelled()) {
            this.setHealth(this.getHealth() + (float) event.getAmount());
        }
    }

    @Inject(method = "heal", at = @At(value = "RETURN"))
    public void neotenet$resetReason(float healAmount, CallbackInfo ci) {
        neotenet$regainReason = null;
    }

    @Redirect(method = "die",
            at = @At(value = "INVOKE",
                    target = "Lorg/slf4j/Logger;info(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V",
                    remap = false))
    private void neotenet$logNamedDeaths(Logger instance, String s, Object o1, Object o2) {
        if (org.spigotmc.SpigotConfig.logNamedDeaths)
            LOGGER.info("Named entity {} died: {}", (Object) this, this.getCombatTracker().getDeathMessage().getString()); // Spigot
    }

    @Override
    public boolean addEffect(MobEffectInstance effect, EntityPotionEffectEvent.Cause cause) {
        pushEffectCause(cause);
        return this.addEffect(effect, (Entity) null, cause);
    }

    @Override
    public boolean removeAllEffects(EntityPotionEffectEvent.Cause cause) {
        pushEffectCause(cause);
        return this.removeAllEffects();
    }

    @Inject(method = "createWitherRose", cancellable = true, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private void neotenet$witherRoseDrop(LivingEntity livingEntity, CallbackInfo ci, @Local ItemEntity
            itemEntity) {
        org.bukkit.event.entity.EntityDropItemEvent event = new org.bukkit.event.entity.EntityDropItemEvent(this.getBukkitEntity(), (org.bukkit.entity.Item) itemEntity.getBukkitEntity());
        CraftEventFactory.callEvent(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Redirect(method = "createWitherRose", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private boolean neotenet$fireWitherRoseForm(Level instance, BlockPos pPos, BlockState pNewState, int pFlags) {
        return CraftEventFactory.handleBlockFormEvent(instance, pPos, pNewState, 3, (Entity) this);
    }

    @Redirect(method = "updateFallFlying", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setSharedFlag(IZ)V"))
    public void neotenet$toggleGlide(LivingEntity livingEntity, int flag, boolean set) {
        if (set != livingEntity.getSharedFlag(flag) && !CraftEventFactory.callToggleGlideEvent(livingEntity, set).isCancelled()) {
            livingEntity.setSharedFlag(flag, set);
        }
    }

    /**
     * @author wdog5
     * @reason
     */
    @Overwrite
    public boolean isPickable() {
        return !this.isRemoved() && this.collides;
    }

    @Inject(method = "addEatEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z"))
    public void neotenet$foodEffectCause(FoodProperties foodProperties, CallbackInfo ci) {
        ((LivingEntity) (Object) this).pushEffectCause(EntityPotionEffectEvent.Cause.FOOD);
    }

    @Inject(method = "setArrowCount", cancellable = true, at = @At("HEAD"))
    private void neotenet$onArrowChange(int count, CallbackInfo ci) {
        if (neotenet$callArrowCountChange(count, false)) {
            ci.cancel();
        }
    }

    @Override
    public void pushEffectCause(EntityPotionEffectEvent.Cause cause) {
        this.neotenet$cause = cause;
    }

    @Override
    public final void setArrowCount(int count, boolean reset) {
        if (neotenet$callArrowCountChange(count, reset)) {
            return;
        }
        this.entityData.set(DATA_ARROW_COUNT_ID, count);
    }

    private boolean neotenet$callArrowCountChange(int newCount, boolean reset) {
        return CraftEventFactory.callArrowBodyCountChangeEvent((LivingEntity) (Object) this, this.getArrowCount(), newCount, reset).isCancelled();
    }

    @Override
    public void equipEventAndSound(EquipmentSlot slot, ItemStack oldItem, ItemStack newItem, boolean silent) {
        boolean flag = newItem.isEmpty() && oldItem.isEmpty();
        if (!flag && !ItemStack.isSameItem(oldItem, newItem) && !this.firstTick) {
            Equipable equipable = Equipable.get(newItem);
            if (equipable != null && !this.isSpectator() && equipable.getEquipmentSlot() == slot) {
                if (!this.level().isClientSide() && !this.isSilent() && !silent) {
                    this.level().playSound(null, this.getX(), this.getY(), this.getZ(), equipable.getEquipSound().value(), this.getSoundSource(), 1.0F, 1.0F);
                }

                if (this.doesEmitEquipEvent(slot)) {
                    this.gameEvent(GameEvent.EQUIP);
                }
            }

        }
    }

    @Override
    public void setItemSlot(EquipmentSlot slotIn, ItemStack stack, boolean silent) {
        this.setItemSlot(slotIn, stack, silent);
    }

    @Override
    public void onEquipItem(EquipmentSlot enumitemslot, ItemStack itemstack, ItemStack itemstack1, boolean silent) {
        neotenet$silent.set(silent);
        this.onEquipItem(enumitemslot, itemstack, itemstack1);
    }

    @Override
    public SoundEvent getHurtSound0(DamageSource damagesource) {
        return InjectionLivingEntity.super.getHurtSound0(damagesource);
    }

    @Override
    public SoundEvent getDeathSound0() {
        return getDeathSound();
    }

    @Override
    public SoundEvent getFallDamageSound0(int fallHeight) {
        return getFallDamageSound(fallHeight);
    }

    @Override
    public SoundEvent getDrinkingSound0(ItemStack itemstack) {
        return getDrinkingSound(itemstack);
    }

    @Override
    public SoundEvent getEatingSound0(ItemStack itemstack) {
        return getEatingSound(itemstack);
    }

    @Override
    public float getBukkitYaw() {
        return getYHeadRot();
    }

    @Override
    public Optional<EntityPotionEffectEvent.Cause> getEffectCause() {
        try {
            return Optional.ofNullable(neotenet$cause);
        } finally {
            neotenet$cause = null;
        }
    }
}