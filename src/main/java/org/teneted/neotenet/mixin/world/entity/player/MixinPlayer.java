package org.teneted.neotenet.mixin.world.entity.player;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.util.Either;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityExhaustionEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.scoreboard.Team;
import org.spigotmc.SpigotWorldConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.teneted.neotenet.injection.world.entity.player.InjectionPlayer;

//TODO fix inject methods
@Mixin(Player.class)
public abstract class MixinPlayer extends LivingEntity implements InjectionPlayer {

    // Banner start
    public AtomicBoolean spawnEntityFromShoulder = new AtomicBoolean(true);
    @Shadow
    protected FoodData foodData;
    @Shadow
    protected PlayerEnderChestContainer enderChestInventory;
    protected AtomicReference<Boolean> neotenet$forceSleep = new AtomicReference<>();
    protected AtomicBoolean startSleepInBed_force = new AtomicBoolean(false);
    @Shadow
    @Final
    private Abilities abilities;
    @Shadow
    private long timeEntitySatOnShoulder;
    @Shadow
    @Final
    private Inventory inventory;
    private EntityExhaustionEvent.ExhaustionReason neotenet$exhaustReason;

    protected MixinPlayer(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    public abstract void tick();

    @Shadow
    public abstract CompoundTag getShoulderEntityLeft();

    @Shadow
    public abstract void setShoulderEntityLeft(CompoundTag entityCompound);

    @Shadow
    public abstract CompoundTag getShoulderEntityRight();

    @Shadow
    public abstract void setShoulderEntityRight(CompoundTag entityCompound);

    @Shadow
    public abstract Either<Player.BedSleepingProblem, Unit> startSleepInBed(BlockPos bedPos);

    @Shadow
    public abstract boolean blockActionRestricted(Level level, BlockPos pos, GameType gameMode);

    @Shadow
    public abstract FoodData getFoodData();

    @Inject(method = "<init>", at = @At("RETURN"))
    private void neotenet$init(CallbackInfo ci) {
        this.foodData.entityhuman = ((Player) (Object) this);
        this.enderChestInventory.setOwner(this.getBukkitEntity());
    }

    @Inject(method = "turtleHelmetTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z"))
    private void neotenet$turtleHelmet(CallbackInfo ci) {
        pushEffectCause(EntityPotionEffectEvent.Cause.TURTLE_HELMET);
    }

    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;heal(F)V"))
    private void neotenet$healByRegen(CallbackInfo ci) {
        pushHealReason(EntityRegainHealthEvent.RegainReason.REGEN);
    }

    @Inject(method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;",
            cancellable = true, at = @At(value = "RETURN", ordinal = 1))
    private void neotenet$playerDropItem(ItemStack droppedItem, boolean dropAround, boolean traceItem, CallbackInfoReturnable<ItemEntity> cir, @Local(ordinal = 0) ItemEntity itemEntity) {
        org.bukkit.entity.Player player = (org.bukkit.entity.Player) this.getBukkitEntity();
        org.bukkit.entity.Item drop = (org.bukkit.entity.Item) itemEntity.getBukkitEntity();

        PlayerDropItemEvent event = new PlayerDropItemEvent(player, drop);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            org.bukkit.inventory.ItemStack cur = player.getInventory().getItemInHand();
            if (traceItem && (cur == null || cur.getAmount() == 0)) {
                // The complete stack was dropped
                player.getInventory().setItemInHand(drop.getItemStack());
            } else if (traceItem && cur.isSimilar(drop.getItemStack()) && cur.getAmount() < cur.getMaxStackSize() && drop.getItemStack().getAmount() == 1) {
                // Only one item is dropped
                cur.setAmount(cur.getAmount() + 1);
                player.getInventory().setItemInHand(cur);
            } else {
                // Fallback
                player.getInventory().addItem(drop.getItemStack());
            }
            cir.setReturnValue(null);
        }
    }

    @Override
    public ItemEntity drop(ItemStack p_36179_, boolean p_36180_, boolean p_36181_, boolean callEvent) {
        if (p_36179_.isEmpty()) {
            return null;
        } else {
            if (this.level().isClientSide) {
                this.swing(InteractionHand.MAIN_HAND);
            }

            double d0 = this.getEyeY() - 0.3F;
            ItemEntity itementity = new ItemEntity(this.level(), this.getX(), d0, this.getZ(), p_36179_);
            itementity.setPickUpDelay(40);
            if (p_36181_) {
                itementity.setThrower(this);
            }

            if (p_36180_) {
                float f = this.random.nextFloat() * 0.5F;
                float f1 = this.random.nextFloat() * (float) (Math.PI * 2);
                itementity.setDeltaMovement((double)(-Mth.sin(f1) * f), 0.2F, (double)(Mth.cos(f1) * f));
            } else {
                float f7 = 0.3F;
                float f8 = Mth.sin(this.getXRot() * (float) (Math.PI / 180.0));
                float f2 = Mth.cos(this.getXRot() * (float) (Math.PI / 180.0));
                float f3 = Mth.sin(this.getYRot() * (float) (Math.PI / 180.0));
                float f4 = Mth.cos(this.getYRot() * (float) (Math.PI / 180.0));
                float f5 = this.random.nextFloat() * (float) (Math.PI * 2);
                float f6 = 0.02F * this.random.nextFloat();
                itementity.setDeltaMovement(
                        (double)(-f3 * f2 * 0.3F) + Math.cos((double)f5) * (double)f6,
                        (double)(-f8 * 0.3F + 0.1F + (this.random.nextFloat() - this.random.nextFloat()) * 0.1F),
                        (double)(f4 * f2 * 0.3F) + Math.sin((double)f5) * (double)f6
                );
            }

            // CraftBukkit start - fire PlayerDropItemEvent
            if (!callEvent) { // SPIGOT-2942: Add boolean to call event
                return itementity;
            }
            org.bukkit.entity.Player player = (org.bukkit.entity.Player) this.getBukkitEntity();
            org.bukkit.entity.Item drop = (org.bukkit.entity.Item) itementity.getBukkitEntity();

            PlayerDropItemEvent event = new PlayerDropItemEvent(player, drop);
            this.level().getCraftServer().getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                org.bukkit.inventory.ItemStack cur = player.getInventory().getItemInHand();
                if (p_36181_ && (cur == null || cur.getAmount() == 0)) {
                    // The complete stack was dropped
                    player.getInventory().setItemInHand(drop.getItemStack());
                } else if (p_36181_ && cur.isSimilar(drop.getItemStack()) && cur.getAmount() < cur.getMaxStackSize() && drop.getItemStack().getAmount() == 1) {
                    // Only one item is dropped
                    cur.setAmount(cur.getAmount() + 1);
                    player.getInventory().setItemInHand(cur);
                } else {
                    // Fallback
                    player.getInventory().addItem(drop.getItemStack());
                }
                return null;
            }
            // CraftBukkit end

            return itementity;
        }
    }

    /**
     * @author wdog5
     * @reason
     */
    @Overwrite
    public boolean canHarmPlayer(final Player entityhuman) {
        Team team;
        if (entityhuman instanceof ServerPlayer thatPlayer) {
            team = thatPlayer.getBukkitEntity().getScoreboard().getPlayerTeam((thatPlayer.getBukkitEntity()));
            if (team == null || team.allowFriendlyFire()) {
                return true;
            }
        } else {
            final OfflinePlayer thisPlayer = Bukkit.getOfflinePlayer(entityhuman.getScoreboardName());
            team = Bukkit.getScoreboardManager().getMainScoreboard().getPlayerTeam(thisPlayer);
            if (team == null || team.allowFriendlyFire()) {
                return true;
            }
        }
        if (((Player) (Object) this) instanceof ServerPlayer) {
            return !team.hasPlayer(((ServerPlayer) (Object) this).getBukkitEntity());
        }
        return !team.hasPlayer(Bukkit.getOfflinePlayer(this.getScoreboardName()));
    }

    /**
     * @author wdog5
     * @reason bukkit
     */
    @Overwrite
    protected void removeEntitiesOnShoulder() {
        if (this.timeEntitySatOnShoulder + 20L < this.level().getGameTime()) {
            if (this.spawnEntityFromShoulder(this.getShoulderEntityLeft())) {
                this.setShoulderEntityLeft(new CompoundTag());
            }
            if (this.spawnEntityFromShoulder(this.getShoulderEntityRight())) {
                this.setShoulderEntityRight(new CompoundTag());
            }
        }
    }

    /**
     * @author wdog5
     * @reason bukkit
     */
    @Overwrite
    private void respawnEntityOnShoulder(CompoundTag entityCompound) {
        if (!this.level().isClientSide && !entityCompound.isEmpty()) {
            EntityType.create(entityCompound, this.level()).ifPresent((entity) -> { // CraftBukkit
                if (entity instanceof TamableAnimal) {
                    ((TamableAnimal) entity).setOwnerUUID(this.uuid);
                }

                entity.setPos(this.getX(), this.getY() + 0.699999988079071D, this.getZ());
                boolean canAdd = ((ServerLevel) this.level()).addWithUUID(entity);
                spawnEntityFromShoulder.set(canAdd);
            }); // CraftBukkit
        }

    }

    @Override
    public boolean spawnEntityFromShoulder(CompoundTag nbttagcompound) {
        respawnEntityOnShoulder(nbttagcompound);
        return spawnEntityFromShoulder.getAndSet(true);
    }

    @Redirect(method = "causeFoodExhaustion", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;addExhaustion(F)V"))
    private void neotenet$exhaustEvent(FoodData foodData, float amount) {
        EntityExhaustionEvent.ExhaustionReason reason = neotenet$exhaustReason == null ? EntityExhaustionEvent.ExhaustionReason.UNKNOWN : neotenet$exhaustReason;
        neotenet$exhaustReason = null;
        EntityExhaustionEvent event = CraftEventFactory.callPlayerExhaustionEvent((Player) (Object) this, reason, amount);
        if (!event.isCancelled()) {
            this.foodData.addExhaustion(event.getExhaustion());
        }
    }

    @Override
    public void setItemSlot(EquipmentSlot slot, ItemStack stack, boolean silent) {
        this.verifyEquippedItem(stack);
        if (slot == EquipmentSlot.MAINHAND) {
            this.equipEventAndSound(slot, this.inventory.items.set(this.inventory.selected, stack), stack, silent);
        } else if (slot == EquipmentSlot.OFFHAND) {
            this.equipEventAndSound(slot, this.inventory.offhand.set(0, stack), stack, silent);
        } else if (slot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR) {
            this.equipEventAndSound(slot, this.inventory.armor.set(slot.getIndex(), stack), stack, silent);
        }
    }

    @Override
    public Either<Player.BedSleepingProblem, Unit> startSleepInBed(BlockPos blockposition, boolean force) {
        neotenet$forceSleep.set(force);
        try {
            return this.startSleepInBed(blockposition);
        } finally {
            this.neotenet$forceSleep.set(false);
        }
    }

    @Inject(method = "stopSleepInBed", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/player/Player;sleepCounter:I"))
    private void neotenet$wakeup(boolean flag, boolean flag1, CallbackInfo ci) {
        BlockPos blockPos = this.getSleepingPos().orElse(null);
        if (this.getBukkitEntity() instanceof org.bukkit.entity.Player player) {
            org.bukkit.block.Block bed;
            if (blockPos != null) {
                bed = CraftBlock.at(this.level(), blockPos);
            } else {
                bed = this.level().getWorld().getBlockAt(player.getLocation());
            }
            PlayerBedLeaveEvent event = new PlayerBedLeaveEvent(player, bed, true);
            Bukkit.getPluginManager().callEvent(event);
        }
    }

    @ModifyArg(method = "jumpFromGround", index = 0, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;causeFoodExhaustion(F)V"))
    private float neotenet$exhaustInfo(float f) {
        SpigotWorldConfig config = level().spigotConfig;
        if (config != null) {
            if (this.isSprinting()) {
                f = config.jumpSprintExhaustion;
                pushExhaustReason(EntityExhaustionEvent.ExhaustionReason.JUMP_SPRINT);
            } else {
                f = config.jumpWalkExhaustion;
                pushExhaustReason(EntityExhaustionEvent.ExhaustionReason.JUMP);
            }
        }
        return f;
    }

    @Redirect(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;setSharedFlag(IZ)V"))
    private void neotenet$toggleGlide(Player playerEntity, int flag, boolean set) {
        if (playerEntity.getSharedFlag(flag) != set && !CraftEventFactory.callToggleGlideEvent((Player) (Object) this, set).isCancelled()) {
            playerEntity.setSharedFlag(flag, set);
        }
    }

    @Inject(method = "startFallFlying", cancellable = true, at = @At("HEAD"))
    private void neotenet$startGlidingEvent(CallbackInfo ci) {
        if (CraftEventFactory.callToggleGlideEvent((Player) (Object) this, true).isCancelled()) {
            this.setSharedFlag(7, true);
            this.setSharedFlag(7, false);
            ci.cancel();
        }
    }

    @Inject(method = "stopFallFlying", cancellable = true, at = @At("HEAD"))
    private void neotenet$stopGlidingEvent(CallbackInfo ci) {
        if (CraftEventFactory.callToggleGlideEvent((Player) (Object) this, false).isCancelled()) {
            ci.cancel();
        }
    }

    @Override
    public CraftHumanEntity getBukkitEntity() {
        return (CraftHumanEntity) super.getBukkitEntity();
    }

    @Override
    public Player forceSleepInBed(boolean force) {
        startSleepInBed_force.set(force);
        return ((Player) (Object) this);
    }
}
