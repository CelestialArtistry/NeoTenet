package org.teneted.neotenet.mixin.world.entity;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.Nameable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PortalProcessor;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.event.CraftPortalEvent;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Item;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityAirChangeEvent;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.entity.EntityPoseChangeEvent;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.entity.EntityUnleashEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleBlockCollisionEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spigotmc.event.entity.EntityMountEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.teneted.neotenet.injection.world.entity.InjectionEntity;

// Banner - TODO fix patches
@Mixin(Entity.class)
public abstract class MixinEntity implements Nameable, EntityAccess, CommandSource, InjectionEntity {

    @Shadow
    @Final
    public static int TOTAL_AIR_SUPPLY;
    @Shadow
    @Final
    private static EntityDataAccessor<Integer> DATA_AIR_SUPPLY_ID;
    @Shadow
    public boolean horizontalCollision;
    @Shadow
    public int tickCount;
    @Shadow
    public ImmutableList<Entity> passengers;
    @Shadow
    @Final
    protected SynchedEntityData entityData;
    @Shadow
    private Level level;
    @Shadow
    private float yRot;
    @Shadow
    private AABB bb;
    @Shadow
    @Nullable
    private Entity.RemovalReason removalReason;
    private transient EntityRemoveEvent.Cause neotenet$removeCause;
    private transient CreatureSpawnEvent.SpawnReason neotenet$spawnReason;
    private final AtomicReference<Vec3> neotenet$location = new AtomicReference<>();

    @Shadow
    public abstract double getX();

    @Shadow
    public abstract double getZ();

    @Shadow
    protected abstract SoundEvent getSwimSound();

    @Shadow
    protected abstract SoundEvent getSwimSplashSound();

    @Shadow
    protected abstract SoundEvent getSwimHighSpeedSplashSound();

    @Shadow
    public abstract boolean isPushable();

    @Shadow
    public abstract Pose getPose();

    @Shadow
    public abstract String getScoreboardName();

    @Shadow
    public abstract double getY();

    @Shadow
    public abstract float getYRot();

    @Shadow
    public abstract float getXRot();

    @Shadow
    public abstract void setInvisible(boolean invisible);

    @Shadow
    public abstract SynchedEntityData getEntityData();

    @Shadow
    public abstract int getAirSupply();

    @Shadow
    public abstract boolean isSwimming();

    @Shadow
    public abstract boolean fireImmune();

    @Shadow
    public abstract boolean hurt(DamageSource source, float amount);

    @Shadow
    public abstract DamageSources damageSources();

    @Shadow
    public abstract boolean teleportTo(ServerLevel level, double x, double y, double z, Set<RelativeMovement> relativeMovements, float yRot, float xRot);

    @Shadow
    public abstract Level level();

    @Shadow
    public abstract Vec3 getDeltaMovement();

    @Shadow
    public abstract void setDeltaMovement(Vec3 deltaMovement);

    @Shadow
    public abstract boolean isRemoved();

    @Shadow
    public abstract void unRide();

    @Shadow
    public abstract EntityType<?> getType();
    @Shadow
    public abstract void moveTo(Vec3 vec);

    @Shadow
    public abstract void moveTo(double x, double y, double z, float yRot, float xRot);

    @Shadow
    public abstract void positionRider(Entity passenger);

    @Shadow
    public abstract boolean getSharedFlag(int p_20292_);

    @Shadow
    public abstract void gameEvent(Holder<GameEvent> holder, @Nullable Entity entity);

    @Shadow
    public abstract void remove(Entity.RemovalReason removalReason);

    @Shadow
    public abstract void igniteForSeconds(float f);

    @Shadow
    protected abstract void handlePortal();

    @Shadow
    @Nullable
    public abstract Entity changeDimension(DimensionTransition dimensionTransition);

    @Shadow protected abstract void readAdditionalSaveData(CompoundTag compoundTag);

    @Shadow
    private CraftEntity bukkitEntity;

    @Shadow
    public int maxAirTicks;

    @Shadow
    public boolean persist;

    @Shadow
    public boolean visibleByDefault;

    @Shadow
    public boolean persistentInvisibility;

    @Shadow
    public boolean valid;

    @Shadow
    @javax.annotation.Nullable
    public PortalProcessor portalProcess;

    @Shadow
    public boolean inWorld;
    @Shadow
    private Vector origin;
    @Shadow
    private UUID originWorld;

    @Shadow
    public BlockPos lastLavaContact;

    @Shadow
    public abstract boolean isInLava();

    @Shadow
    private int remainingFireTicks;

    @Shadow
    public abstract void igniteForTicks(int p_320711_);

    @Shadow
    public abstract CompoundTag saveWithoutId(CompoundTag p_20241_);

    @Shadow
    @javax.annotation.Nullable
    protected abstract String getEncodeId();

    @Shadow
    @javax.annotation.Nullable
    private Entity vehicle;

    @Shadow
    protected abstract ListTag newDoubleList(double... p_20064_);

    @Shadow
    private float xRot;

    @Shadow
    protected abstract ListTag newFloatList(float... p_20066_);

    @Shadow
    public float fallDistance;

    @Shadow
    public abstract boolean onGround();

    @Shadow
    private boolean invulnerable;

    @Shadow
    private int portalCooldown;

    @Shadow
    @Final
    private static int CURRENT_LEVEL;

    @Shadow
    public abstract int getMaxAirSupply();

    @Shadow
    public abstract boolean isCustomNameVisible();

    @Shadow
    public abstract boolean isSilent();

    @Shadow
    public abstract RegistryAccess registryAccess();

    @Shadow
    public abstract boolean isNoGravity();

    @Shadow
    private boolean hasGlowingTag;

    @Shadow
    public abstract int getTicksFrozen();

    @Shadow
    private boolean hasVisualFire;

    @Shadow
    @Final
    private Set<String> tags;

    @Shadow
    public abstract boolean isVehicle();

    @Shadow
    public abstract List<Entity> getPassengers();

    @Shadow
    public abstract void fillCrashReportCategory(CrashReportCategory p_20051_);

    @Shadow
    protected abstract void addAdditionalSaveData(CompoundTag p_20139_);

    @Override
    public void setOrigin(@NotNull Location location) {
        this.origin = location.toVector();
        this.originWorld = location.getWorld().getUID();
    }

    @Nullable
    @Override
    public Vector getOriginVector() {
        return this.origin != null ? this.origin.clone() : null;
    }

    @Nullable
    @Override
    public UUID getOriginWorld() {
        return this.originWorld;
    }

    @Override
    public CraftEntity getBukkitEntity() {
        if (bukkitEntity == null) {
            bukkitEntity = CraftEntity.getEntity(level.getCraftServer(), ((Entity) (Object) this));
        }
        return bukkitEntity;
    }

    @Override
    public CommandSender getBukkitSender(CommandSourceStack wrapper) {
        return getBukkitEntity();
    }

    @Override
    public int getDefaultMaxAirSupply() {
        return TOTAL_AIR_SUPPLY;
    }

    @Override
    public float getBukkitYaw() {
        return this.yRot;
    }

    @Override
    public boolean isChunkLoaded() {
        return level.hasChunk((int) Math.floor(this.getX()) >> 4, (int) Math.floor(this.getZ()) >> 4);
    }

    @Override
    public void postTick() {
        // No clean way to break out of ticking once the entity has been copied to a new world, so instead we move the portalling later in the tick cycle
        if (!(((Entity) (Object) this) instanceof ServerPlayer)) {
            this.handlePortal();
        }
    }

    private AtomicBoolean neotenet$callEvent = new AtomicBoolean(true);

    @Inject(method = "igniteForSeconds", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;igniteForTicks(I)V"))
    private void neotenet$callEntityCombustEvent(float p_345382_, CallbackInfo ci) {
        if (neotenet$callEvent.get()) {
            EntityCombustEvent event = new EntityCombustEvent(this.getBukkitEntity(), p_345382_);
            this.level.getCraftServer().getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                return;
            }

            p_345382_ = event.getDuration();
        }
    }

    @Override
    public void igniteForSeconds(float i, boolean callEvent) {
        neotenet$callEvent.set(callEvent);
        if (callEvent) {
            EntityCombustEvent event = new EntityCombustEvent(this.getBukkitEntity(), i);
            this.level.getCraftServer().getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                return;
            }

            i = event.getDuration();
        }
        this.igniteForTicks(Mth.floor(i * 20.0F));
    }

    @Override
    public SoundEvent getSwimSound0() {
        return getSwimSound();
    }

    @Override
    public SoundEvent getSwimSplashSound0() {
        return getSwimSplashSound();
    }

    @Override
    public SoundEvent getSwimHighSpeedSplashSound0() {
        return getSwimHighSpeedSplashSound();
    }

    @Override
    public boolean canCollideWithBukkit(Entity entity) {
        return isPushable();
    }

    @WrapWithCondition(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;handlePortal()V"))
    private boolean neotenet$checkIfPlayer(Entity instance) {
        return ((Entity) (Object) this) instanceof ServerPlayer;
    }

    @Inject(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;checkBelowWorld()V"))
    private void neotenet$setlastLavaContact(CallbackInfo ci) {
        if (!this.isInLava()) {
            this.lastLavaContact = null;
        }
    }

    @Inject(method = "checkInsideBlocks", at = @At(value = "TAIL"))
    private void neotenet$checkValid(CallbackInfo ci) {
        if (valid) level.getChunk((int) Math.floor(this.getX()) >> 4, (int) Math.floor(this.getZ()) >> 4); // CraftBukkit
    }

    @Redirect(method = "lavaHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;igniteForSeconds(F)V"))
    private void neotenet$combustEvent(Entity instance, float f) {
        // CraftBukkit start - Fallen in lava TODO: this event spams!
        if (((Entity) (Object) this) instanceof LivingEntity && remainingFireTicks <= 0) {
            // not on fire yet
            org.bukkit.block.Block damager = (lastLavaContact == null) ? null : org.bukkit.craftbukkit.block.CraftBlock.at(level, lastLavaContact);
            org.bukkit.entity.Entity damagee = this.getBukkitEntity();
            EntityCombustEvent combustEvent = new org.bukkit.event.entity.EntityCombustByBlockEvent(damager, damagee, 15);
            this.level.getCraftServer().getPluginManager().callEvent(combustEvent);

            if (!combustEvent.isCancelled()) {
                this.igniteForSeconds(combustEvent.getDuration(), false);
            }
        } else {
            // This will be called every single tick the entity is in lava, so don't throw an event
            this.igniteForSeconds(15.0F, false);
        }
    }

    @ModifyArg(method = "lavaHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"), index = 0)
    private DamageSource neotenet$useDirect(DamageSource p_19946_) {
        return p_19946_.directBlock(level, lastLavaContact);
    }

    @Inject(method = "updateFluidHeightAndDoFluidPushing()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;add(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;"))
    private void neotenet$markLavaType(CallbackInfo ci, @Local(ordinal = 0) net.neoforged.neoforge.fluids.FluidType fluidType, @Local(ordinal = 0) BlockPos.MutableBlockPos blockpos$mutableblockpos) {
        if (fluidType == net.neoforged.neoforge.common.NeoForgeMod.LAVA_TYPE.value()) {
            this.lastLavaContact = blockpos$mutableblockpos.immutable();
        }
    }

    @Inject(method = "getMaxAirSupply", cancellable = true, at = @At("RETURN"))
    private void neotenet$useBukkitMaxAir(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(this.maxAirTicks);
    }

    @Inject(method = "setPose", cancellable = true, at = @At(value = "INVOKE", target = "Lnet/minecraft/network/syncher/SynchedEntityData;set(Lnet/minecraft/network/syncher/EntityDataAccessor;Ljava/lang/Object;)V"))
    public void neotenet$setPose$EntityPoseChangeEvent(Pose pose, CallbackInfo ci) {
        if (pose == this.getPose()) {
            ci.cancel();
            return;
        }
        EntityPoseChangeEvent event = new EntityPoseChangeEvent(this.getBukkitEntity(), org.bukkit.entity.Pose.values()[pose.ordinal()]);
        Bukkit.getPluginManager().callEvent(event);
    }

    @Inject(method = "setRot", at = @At(value = "HEAD"))
    public void neotenet$infCheck(float yaw, float pitch, CallbackInfo ci) {
        // CraftBukkit start - yaw was sometimes set to NaN, so we need to set it back to 0
        if (Float.isNaN(yaw)) {
            yaw = 0;
        }

        if (yaw == Float.POSITIVE_INFINITY || yaw == Float.NEGATIVE_INFINITY) {
            if (((Object) this) instanceof Player) {
                this.level.getCraftServer().getLogger().warning(this.getScoreboardName() + " was caught trying to crash the server with an invalid yaw");
                ((CraftPlayer) this.getBukkitEntity()).kickPlayer("Infinite yaw (Hacking?)");
            }
            yaw = 0;
        }

        // pitch was sometimes set to NaN, so we need to set it back to 0
        if (Float.isNaN(pitch)) {
            pitch = 0;
        }

        if (pitch == Float.POSITIVE_INFINITY || pitch == Float.NEGATIVE_INFINITY) {
            if (((Object) this) instanceof Player) {
                this.level.getCraftServer().getLogger().warning(this.getScoreboardName() + " was caught trying to crash the server with an invalid pitch");
                ((CraftPlayer) this.getBukkitEntity()).kickPlayer("Infinite pitch (Hacking?)");
            }
            pitch = 0;
        }
        // CraftBukkit end
    }

    @Inject(method = "move", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;onGround()Z",
            ordinal = 1))
    private void neotenet$move$blockCollide(MoverType type, Vec3 pos, CallbackInfo ci, @Local(ordinal = 1) Vec3 vec3) {
        // CraftBukkit start
        if (horizontalCollision && getBukkitEntity() instanceof Vehicle) {
            Vehicle vehicle = (Vehicle) this.getBukkitEntity();
            Block cbBlock = this.level.getWorld().getBlockAt(Mth.floor(this.getX()), Mth.floor(this.getY()), Mth.floor(this.getZ()));

            if (pos.x > vec3.x) {
                cbBlock = cbBlock.getRelative(BlockFace.EAST);
            } else if (pos.x < vec3.x) {
                cbBlock = cbBlock.getRelative(BlockFace.WEST);
            } else if (pos.z > vec3.z) {
                cbBlock = cbBlock.getRelative(BlockFace.SOUTH);
            } else if (pos.z < vec3.z) {
                cbBlock = cbBlock.getRelative(BlockFace.NORTH);
            }

            if (!cbBlock.getType().isAir()) {
                VehicleBlockCollisionEvent event = new VehicleBlockCollisionEvent(vehicle, cbBlock);
                level.getCraftServer().getPluginManager().callEvent(event);
            }
        }
        // CraftBukkit end
    }

    @Inject(method = "saveAsPassenger", cancellable = true, at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/entity/Entity;getEncodeId()Ljava/lang/String;"))
    public void neotenet$writeUnlessRemoved$persistCheck(CompoundTag compound, CallbackInfoReturnable<Boolean> cir) {
        if (!this.persist)
            cir.setReturnValue(false);
    }

    @Override
    public boolean saveAsPassenger(CompoundTag nbttagcompound, boolean includeAll) {
        if (this.removalReason != null && !this.removalReason.shouldSave()) {
            return false;
        } else {
            String s = this.getEncodeId();
            if (!this.persist || s == null) {
                return false;
            } else {
                nbttagcompound.putString("id", s);
                this.saveWithoutId(nbttagcompound);
                return true;
            }
        }
    }

    @Override
    public CompoundTag saveWithoutId(CompoundTag nbttagcompound, boolean includeAll) {
        // CraftBukkit end
        try {
            // CraftBukkit start - selectively save position
            if (includeAll) {
                if (this.vehicle != null) {
                    nbttagcompound.put("Pos", this.newDoubleList(this.vehicle.getX(), this.getY(), this.vehicle.getZ()));
                } else {
                    nbttagcompound.put("Pos", this.newDoubleList(this.getX(), this.getY(), this.getZ()));
                }
            }
            // CraftBukkit end

            Vec3 vec3d = this.getDeltaMovement();

            nbttagcompound.put("Motion", this.newDoubleList(vec3d.x, vec3d.y, vec3d.z));

            // CraftBukkit start - Checking for NaN pitch/yaw and resetting to zero
            // TODO: make sure this is the best way to address this.
            if (Float.isNaN(this.yRot)) {
                this.yRot = 0;
            }

            if (Float.isNaN(this.xRot)) {
                this.xRot = 0;
            }
            // CraftBukkit end

            nbttagcompound.put("Rotation", this.newFloatList(this.getYRot(), this.getXRot()));
            nbttagcompound.putFloat("FallDistance", this.fallDistance);
            nbttagcompound.putShort("Fire", (short) this.remainingFireTicks);
            nbttagcompound.putShort("Air", (short) this.getAirSupply());
            nbttagcompound.putBoolean("OnGround", this.onGround());
            nbttagcompound.putBoolean("Invulnerable", this.invulnerable);
            nbttagcompound.putInt("PortalCooldown", this.portalCooldown);
            // CraftBukkit start - selectively save uuid and world
            if (includeAll) {
                nbttagcompound.putUUID("UUID", this.getUUID());
                // PAIL: Check above UUID reads 1.8 properly, ie: UUIDMost / UUIDLeast
                nbttagcompound.putLong("WorldUUIDLeast", ((ServerLevel) this.level).getWorld().getUID().getLeastSignificantBits());
                nbttagcompound.putLong("WorldUUIDMost", ((ServerLevel) this.level).getWorld().getUID().getMostSignificantBits());
            }
            nbttagcompound.putInt("Bukkit.updateLevel", CURRENT_LEVEL);
            if (!this.persist) {
                nbttagcompound.putBoolean("Bukkit.persist", this.persist);
            }
            if (!this.visibleByDefault) {
                nbttagcompound.putBoolean("Bukkit.visibleByDefault", this.visibleByDefault);
            }
            if (this.persistentInvisibility) {
                nbttagcompound.putBoolean("Bukkit.invisible", this.persistentInvisibility);
            }
            // SPIGOT-6907: re-implement LivingEntity#setMaximumAir()
            if (this.maxAirTicks != this.getDefaultMaxAirSupply()) {
                nbttagcompound.putInt("Bukkit.MaxAirSupply", this.getMaxAirSupply());
            }
            nbttagcompound.putInt("Spigot.ticksLived", this.tickCount);
            // CraftBukkit end
            Component ichatbasecomponent = this.getCustomName();

            if (ichatbasecomponent != null) {
                nbttagcompound.putString("CustomName", Component.Serializer.toJson(ichatbasecomponent, this.registryAccess()));
            }

            if (this.isCustomNameVisible()) {
                nbttagcompound.putBoolean("CustomNameVisible", this.isCustomNameVisible());
            }

            if (this.isSilent()) {
                nbttagcompound.putBoolean("Silent", this.isSilent());
            }

            if (this.isNoGravity()) {
                nbttagcompound.putBoolean("NoGravity", this.isNoGravity());
            }

            if (this.hasGlowingTag) {
                nbttagcompound.putBoolean("Glowing", true);
            }

            int i = this.getTicksFrozen();

            if (i > 0) {
                nbttagcompound.putInt("TicksFrozen", this.getTicksFrozen());
            }

            if (this.hasVisualFire) {
                nbttagcompound.putBoolean("HasVisualFire", this.hasVisualFire);
            }

            ListTag nbttaglist;
            Iterator iterator;

            if (!this.tags.isEmpty()) {
                nbttaglist = new ListTag();
                iterator = this.tags.iterator();

                while (iterator.hasNext()) {
                    String s = (String) iterator.next();

                    nbttaglist.add(StringTag.valueOf(s));
                }

                nbttagcompound.put("Tags", nbttaglist);
            }

            this.addAdditionalSaveData(nbttagcompound, includeAll); // CraftBukkit - pass on includeAll
            if (this.isVehicle()) {
                nbttaglist = new ListTag();
                iterator = this.getPassengers().iterator();

                while (iterator.hasNext()) {
                    Entity entity = (Entity) iterator.next();
                    CompoundTag nbttagcompound1 = new CompoundTag();

                    if (entity.saveAsPassenger(nbttagcompound1, includeAll)) { // CraftBukkit - pass on includeAll
                        nbttaglist.add(nbttagcompound1);
                    }
                }

                if (!nbttaglist.isEmpty()) {
                    nbttagcompound.put("Passengers", nbttaglist);
                }
            }

            // CraftBukkit start - stores eventually existing bukkit values
            if (this.bukkitEntity != null) {
                this.bukkitEntity.storeBukkitValues(nbttagcompound);
            }
            // CraftBukkit end
            return nbttagcompound;
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.forThrowable(throwable, "Saving entity NBT");
            CrashReportCategory crashreportsystemdetails = crashreport.addCategory("Entity being saved");

            this.fillCrashReportCategory(crashreportsystemdetails);
            throw new ReportedException(crashreport);
        }
    }

    // CraftBukkit start - allow excluding certain data when saving
    @Override
    public void addAdditionalSaveData(CompoundTag nbttagcompound, boolean includeAll) {
        addAdditionalSaveData(nbttagcompound);
    }
    // CraftBukkit end

    @Inject(method = "load", at = @At(value = "RETURN"))
    private void neotenet$read$ReadBukkitValues(CompoundTag compound, CallbackInfo ci) {
        // CraftBukkit start
        if ((Object) this instanceof LivingEntity entity) {
            this.tickCount = compound.getInt("Spigot.ticksLived");
        }
        this.persist = !compound.contains("Bukkit.persist") || compound.getBoolean("Bukkit.persist");
        this.visibleByDefault = !compound.contains("Bukkit.visibleByDefault") || compound.getBoolean("Bukkit.visibleByDefault");
        // CraftBukkit end

        // CraftBukkit start - Reset world
        if ((Object) this instanceof ServerPlayer) {
            Server server = Bukkit.getServer();
            World bworld = null;

            String worldName = compound.getString("world");

            if (compound.contains("WorldUUIDMost") && compound.contains("WorldUUIDLeast")) {
                UUID uid = new UUID(compound.getLong("WorldUUIDMost"), compound.getLong("WorldUUIDLeast"));
                bworld = server.getWorld(uid);
            } else {
                bworld = server.getWorld(worldName);
            }

            if (bworld == null) {
                bworld = (((CraftServer) server).getServer().getLevel(Level.OVERWORLD)).getWorld();
            }

            ((ServerPlayer) (Object) this).setServerLevel(bworld == null ? null : ((CraftWorld) bworld).getHandle());
        }
        this.getBukkitEntity().readBukkitValues(compound);
        if (compound.contains("Bukkit.invisible")) {
            boolean bukkitInvisible = compound.getBoolean("Bukkit.invisible");
            this.setInvisible(bukkitInvisible);
            this.persistentInvisibility = bukkitInvisible;
        }
        if (compound.contains("Bukkit.MaxAirSupply")) {
            maxAirTicks = compound.getInt("Bukkit.MaxAirSupply");
        }
        // CraftBukkit end
        // Paper start - Restore the entity's origin location
        ListTag originTag = compound.getList("Paper.Origin", 6);
        if (!originTag.isEmpty()) {
            UUID originWorld = null;
            if (compound.contains("Paper.OriginWorld")) {
                originWorld = compound.getUUID("Paper.OriginWorld");
            } else if (this.level != null) {
                originWorld = this.level.getWorld().getUID();
            }
            this.originWorld = originWorld;
            origin = new Vector(originTag.getDouble(0), originTag.getDouble(1), originTag.getDouble(2));
        }
        // Paper end
    }

    @Inject(method = "setInvisible", cancellable = true, at = @At("HEAD"))
    private void neotenet$preventVisible(boolean invisible, CallbackInfo ci) {
        if (this.persistentInvisibility) {
            ci.cancel();
        }
    }

    @Inject(method = "spawnAtLocation(Lnet/minecraft/world/item/ItemStack;F)Lnet/minecraft/world/entity/item/ItemEntity;",
            cancellable = true,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    public void neotenet$entityDropItem(ItemStack stack, float offsetY, CallbackInfoReturnable<ItemEntity> cir, @Local ItemEntity itemEntity) {
        EntityDropItemEvent event = new EntityDropItemEvent(this.getBukkitEntity(), (Item) (itemEntity).getBukkitEntity());
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            cir.setReturnValue(null);
        }
    }

    @Inject(method = "startRiding(Lnet/minecraft/world/entity/Entity;Z)Z", cancellable = true, at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Lnet/minecraft/world/entity/Entity;setPose(Lnet/minecraft/world/entity/Pose;)V"))
    public void neotenet$startRiding(Entity vehicle, boolean force, CallbackInfoReturnable<Boolean> cir) {
        // CraftBukkit start
        if (vehicle.getBukkitEntity() instanceof Vehicle && this.getBukkitEntity() instanceof org.bukkit.entity.LivingEntity) {
            VehicleEnterEvent event = new VehicleEnterEvent((Vehicle) vehicle.getBukkitEntity(), this.getBukkitEntity());
            // Suppress during worldgen
            if (this.valid) {
                Bukkit.getPluginManager().callEvent(event);
            }
            if (event.isCancelled()) {
                cir.setReturnValue(false);
            }
        }
        // CraftBukkit end
        // Spigot start
        EntityMountEvent event = new EntityMountEvent(this.getBukkitEntity(), vehicle.getBukkitEntity());
        // Suppress during worldgen
        if (this.valid) {
            Bukkit.getPluginManager().callEvent(event);
        }
        if (event.isCancelled()) {
            cir.setReturnValue(false);
        }
        // Spigot end
    }

    @Inject(method = "interact", cancellable = true, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Leashable;setLeashedTo(Lnet/minecraft/world/entity/Entity;Z)V"))
    private void neotenet$leashEvent(Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResult> cir) {
        if (CraftEventFactory.callPlayerLeashEntityEvent((Entity) (Object) this, player, player, interactionHand).isCancelled()) {
            //player.resendItemInHands(); // SPIGOT-7615: Resend to fix client desync with used item // Banner TODO Fixme
            ((ServerPlayer) player).connection.send(new ClientboundSetEntityLinkPacket((Entity) (Object) this, ((Leashable) this).getLeashHolder()));
            cir.setReturnValue(InteractionResult.PASS);
        }
    }

    @Inject(method = "setSwimming", cancellable = true, at = @At(value = "HEAD"))
    public void neotenet$setSwimming$EntityToggleSwimEvent(boolean flag, CallbackInfo ci) {
        // CraftBukkit start
        if (this.valid && this.isSwimming() != flag && (Object) this instanceof LivingEntity) {
            if (CraftEventFactory.callToggleSwimEvent((LivingEntity) (Object) this, flag).isCancelled()) {
                ci.cancel();
            }
        }
        // CraftBukkit end
    }

    @Redirect(method = "thunderHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;igniteForSeconds(F)V"))
    public void neotenet$onStruckByLightning$EntityCombustByEntityEvent0(Entity entity, float f) {
        final org.bukkit.entity.Entity thisBukkitEntity = this.getBukkitEntity();
        final org.bukkit.entity.Entity stormBukkitEntity = entity.getBukkitEntity();
        final PluginManager pluginManager = Bukkit.getPluginManager();
        // CraftBukkit start - Call a combust event when lightning strikes
        EntityCombustByEntityEvent entityCombustEvent = new EntityCombustByEntityEvent(stormBukkitEntity, thisBukkitEntity, 8);
        pluginManager.callEvent(entityCombustEvent);
        if (!entityCombustEvent.isCancelled()) {
            this.igniteForSeconds(entityCombustEvent.getDuration());
        }
        // CraftBukkit end
    }

    @Inject(method = "setAirSupply", cancellable = true, at = @At(value = "HEAD"))
    public void neotenet$setAir$EntityAirChangeEvent(int air, CallbackInfo ci) {
        // CraftBukkit start
        EntityAirChangeEvent event = new EntityAirChangeEvent(this.getBukkitEntity(), air);
        // Suppress during worldgen
        if (this.valid) {
            event.getEntity().getServer().getPluginManager().callEvent(event);
        }
        if (event.isCancelled() && this.getAirSupply() != -1) {
            ci.cancel();
            this.getEntityData().markDirty(DATA_AIR_SUPPLY_ID);
            return;
        }
        this.entityData.set(DATA_AIR_SUPPLY_ID, event.getAmount());
        // CraftBukkit end
    }

    @Redirect(method = "thunderHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    public boolean neotenet$onStruckByLightning$EntityCombustByEntityEvent1(Entity instance, DamageSource source, float amount) {
        final org.bukkit.entity.Entity thisBukkitEntity = this.getBukkitEntity();
        final org.bukkit.entity.Entity stormBukkitEntity = instance.getBukkitEntity();
        final PluginManager pluginManager = Bukkit.getPluginManager();
        if (thisBukkitEntity instanceof Hanging) {
            HangingBreakByEntityEvent hangingEvent = new HangingBreakByEntityEvent((Hanging) thisBukkitEntity, stormBukkitEntity);
            pluginManager.callEvent(hangingEvent);

            if (hangingEvent.isCancelled()) {
                return false;
            }
        }

        if (this.fireImmune()) {
            return false;
        }
        return this.hurt(this.damageSources().lightningBolt(), amount);
    }

    @Inject(method = "changeDimension", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/portal/DimensionTransition;newLevel()Lnet/minecraft/server/level/ServerLevel;"), cancellable = true)
    private void neotenet$callEntityTeleportEvent(DimensionTransition p_350951_, CallbackInfoReturnable<Entity> cir) {
        // CraftBukkit start
        Location to = new Location(p_350951_.newLevel().getWorld(), p_350951_.pos().x, p_350951_.pos().y, p_350951_.pos().z, p_350951_.yRot(), p_350951_.xRot());
        EntityTeleportEvent teleEvent = CraftEventFactory.callEntityTeleportEvent(((Entity) (Object) this), to);
        if (teleEvent.isCancelled()) {
            cir.setReturnValue(null);
        }
        to = teleEvent.getTo();
        p_350951_ = new DimensionTransition(((CraftWorld) to.getWorld()).getHandle(), CraftLocation.toVec3D(to), p_350951_.speed(), to.getYaw(), to.getPitch(), p_350951_.missingRespawnBlock(), p_350951_.postDimensionTransition(), p_350951_.cause());
        // CraftBukkit end
    }

    @Inject(method = "removeAfterChangingDimensions", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;setRemoved(Lnet/minecraft/world/entity/Entity$RemovalReason;)V"))
    private void neotenet$pushRemoveCause(CallbackInfo ci) {
        this.pushRemoveCause(null);
    }

    @Inject(method = "removeAfterChangingDimensions", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Leashable;dropLeash(ZZ)V"))
    private void neotenet$callEntityUnleashEvent(CallbackInfo ci) {
        this.level().getCraftServer().getPluginManager().callEvent(new EntityUnleashEvent(this.getBukkitEntity(), EntityUnleashEvent.UnleashReason.UNKNOWN)); // CraftBukkit
    }


    @Nullable
    @Override
    public Entity teleportTo(ServerLevel worldserver, Vec3 location) {
        neotenet$location.set(location);
        DimensionTransition dimensionTransition = this.portalProcess.getPortalDestination(worldserver, ((Entity) (Object) this));
        return changeDimension(dimensionTransition);
    }

    @Override
    public boolean teleportTo(ServerLevel worldserver, double d0, double d1, double d2, Set<RelativeMovement> set, float f, float f1, PlayerTeleportEvent.TeleportCause cause) {
        return this.teleportTo(worldserver, d0, d1, d2, set, f, f1);
    }

    @Redirect(method = "teleportTo(Lnet/minecraft/server/level/ServerLevel;DDDLjava/util/Set;FF)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addDuringTeleport(Lnet/minecraft/world/entity/Entity;)V"))
    private void neotenet$skipIfNotInWorld(ServerLevel instance, Entity entity) {
        if (this.inWorld) {
            instance.addDuringTeleport(entity);
        }
    }

    @Inject(method = "restoreFrom", at = @At("HEAD"))
    private void neotenet$forwardHandle(Entity entityIn, CallbackInfo ci) {
        entityIn.getBukkitEntity().setHandle((Entity) (Object) this);
        this.bukkitEntity = entityIn.getBukkitEntity();
        if (entityIn instanceof Mob) {
            ((Mob) entityIn).dropLeash(true, false);
        }
    }

    @Override
    public CraftPortalEvent callPortalEvent(Entity entity, ServerLevel exitWorldServer, Vec3 exitPosition, PlayerTeleportEvent.TeleportCause cause, int searchRadius, int creationRadius) {
        CraftEntity bukkitEntity = entity.getBukkitEntity();
        Location enter = bukkitEntity.getLocation();
        Location exit = CraftLocation.toBukkit(exitPosition, exitWorldServer.getWorld());
        EntityPortalEvent event = new EntityPortalEvent(bukkitEntity, enter, exit, searchRadius);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled() || event.getTo() == null || event.getTo().getWorld() == null || !entity.isAlive()) {
            return null;
        }
        return new CraftPortalEvent(event);
    }

    @Redirect(method = "setBoundingBox",
            at = @At(value = "FIELD",
                    target = "Lnet/minecraft/world/entity/Entity;bb:Lnet/minecraft/world/phys/AABB;"))
    private void neotenet$resetBBox(Entity instance, AABB axisalignedbb) {
        // CraftBukkit start - block invalid bounding boxes
        double minX = axisalignedbb.minX,
                minY = axisalignedbb.minY,
                minZ = axisalignedbb.minZ,
                maxX = axisalignedbb.maxX,
                maxY = axisalignedbb.maxY,
                maxZ = axisalignedbb.maxZ;
        double len = axisalignedbb.maxX - axisalignedbb.minX;
        if (len < 0) maxX = minX;
        if (len > 64) maxX = minX + 64.0;

        len = axisalignedbb.maxY - axisalignedbb.minY;
        if (len < 0) maxY = minY;
        if (len > 64) maxY = minY + 64.0;

        len = axisalignedbb.maxZ - axisalignedbb.minZ;
        if (len < 0) maxZ = minZ;
        if (len > 64) maxZ = minZ + 64.0;
        this.bb = new AABB(minX, minY, minZ, maxX, maxY, maxZ);
        // CraftBukkit end
    }

    @Override
    public void refreshEntityData(ServerPlayer to) {
        List<SynchedEntityData.DataValue<?>> list = this.getEntityData().getNonDefaultValues();

        if (list != null) {
            to.connection.send(new ClientboundSetEntityDataPacket(this.getId(), list));
        }
    }

    @Override
    public void discard(EntityRemoveEvent.Cause cause) {
        this.remove(Entity.RemovalReason.DISCARDED, cause);
    }

    @Override
    public void remove(Entity.RemovalReason entity_removalreason, EntityRemoveEvent.Cause cause) {
        this.setRemoved(entity_removalreason, cause);
    }

    @Override
    public void setRemoved(Entity.RemovalReason entity_removalreason, EntityRemoveEvent.Cause cause) {
        CraftEventFactory.callEntityRemoveEvent(((Entity) (Object) this), cause);
        this.setRemoved(removalReason);
    }

    @Inject(method = "setRemoved", at = @At("HEAD"))
    private void neotenet$setRemoved(Entity.RemovalReason removalReason, CallbackInfo ci) {
        CraftEventFactory.callEntityRemoveEvent(((Entity) (Object) this), neotenet$removeCause != null ? neotenet$removeCause : null);
    }

    @Override
    public void pushRemoveCause(EntityRemoveEvent.Cause cause) {
        this.neotenet$removeCause = cause;
    }

    @Override
    public void pushSpawnCause(CreatureSpawnEvent.SpawnReason reason) {
        this.neotenet$spawnReason = reason;
    }

    @Inject(method = "spawnAtLocation(Lnet/minecraft/world/item/ItemStack;F)Lnet/minecraft/world/entity/item/ItemEntity;", at = @At("HEAD"))
    private void neotenet$spawnReason(ItemStack itemStack, float f, CallbackInfoReturnable<ItemEntity> cir) {
        pushSpawnCause(CreatureSpawnEvent.SpawnReason.NATURAL);
    }

    @Inject(method = "kill", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;remove(Lnet/minecraft/world/entity/Entity$RemovalReason;)V"))
    private void neotenet$killReason(CallbackInfo ci) {
        pushRemoveCause(EntityRemoveEvent.Cause.DEATH);
    }

    @Inject(method = "onBelowWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;discard()V"))
    private void neotenet$pushOutOfWorldReason(CallbackInfo ci) {
        pushRemoveCause(EntityRemoveEvent.Cause.OUT_OF_WORLD);
    }
}
