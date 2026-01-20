package org.teneted.neotenet.mixin.server.level;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import net.minecraft.BlockUtil;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.ChangeDimensionTrigger;
import net.minecraft.advancements.critereon.DistanceTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.protocol.game.ClientboundSetHealthPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.PlayerRespawnLogic;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.stats.RecipeBook;
import net.minecraft.stats.ServerRecipeBook;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Unit;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.CombatTracker;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerSynchronizer;
import net.minecraft.world.inventory.HorseInventoryMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.ScoreAccess;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.WeatherType;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.CraftWorldBorder;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.event.CraftPortalEvent;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.util.BlockStateListPopulator;
import org.bukkit.craftbukkit.util.CraftDimensionUtil;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.event.entity.EntityExhaustionEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.MainHand;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
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
import org.teneted.neotenet.injection.server.level.InjectionServerPlayer;

@Mixin(ServerPlayer.class)
public abstract class MixinServerPlayer extends Player implements InjectionServerPlayer {

    @Shadow
    public int lastSentExp;
    @Shadow
    @Final
    public MinecraftServer server;
    @Shadow
    @Final
    public ServerPlayerGameMode gameMode;
    @Shadow
    public ServerGamePacketListenerImpl connection;
    // CraftBukkit start
    public String displayName;
    public Component listName;
    public Location compassTarget;
    public int newExp = 0;
    public int newLevel = 0;
    public int newTotalExp = 0;
    public boolean keepLevel = false;
    public double maxHealthCache;
    public boolean joining = true;
    public boolean sentListPacket = false;
    public Integer clientViewDistance;
    public String kickLeaveMessage = null; // SPIGOT-3034: Forward leave message to PlayerQuitEvent
    public long timeOffset = 0;
    public WeatherType weather = null;
    public boolean relativeTime = true;
    public String locale = "en_us"; // CraftBukkit - add, lowercase
    public CraftPlayer.TransferCookieConnection transferCookieConnection;
    @Shadow
    private ResourceKey<Level> respawnDimension;
    @Shadow
    @Nullable
    private Entity camera;
    @Shadow
    private int containerCounter;
    @Shadow
    private boolean respawnForced;
    private boolean neotenet$initialized = false;
    private float pluginRainPosition;
    private float pluginRainPositionPrevious;
    private transient PlayerSpawnChangeEvent.Cause neotenet$spawnChangeCause;
    private final AtomicReference<HorseInventoryMenu> neotenet$horseMenu = new AtomicReference<>();
    private final AtomicReference<PlayerTeleportEvent.TeleportCause> neotenet$changeDimensionCause = new AtomicReference<>(PlayerTeleportEvent.TeleportCause.UNKNOWN);
    // CraftBukkit end
    private transient BlockStateListPopulator neotenet$populator;
    private PlayerDeathEvent event;
    private boolean result;
    private ServerLevel serverLevel;
    private Location exit;

    public MixinServerPlayer(Level level, BlockPos blockPos, float f, GameProfile gameProfile) {
        super(level, blockPos, f, gameProfile);
    }

    @Shadow
    protected abstract boolean bedInRange(BlockPos pos, Direction direction);

    @Shadow
    protected abstract boolean bedBlocked(BlockPos pos, Direction direction);

    @Shadow
    protected abstract int getCoprime(int i);

    @Shadow
    @Nullable
    public abstract BlockPos getRespawnPosition();

    @Shadow
    public abstract float getRespawnAngle();

    @Shadow
    public abstract void setServerLevel(ServerLevel serverLevel);

    @Shadow
    public abstract ServerLevel serverLevel();

    @Shadow
    public abstract void initMenu(AbstractContainerMenu abstractContainerMenu);

    @Shadow
    public abstract boolean teleportTo(ServerLevel level, double x, double y, double z, Set<RelativeMovement> relativeMovements, float yRot, float xRot);

    @Shadow
    public abstract void teleportTo(ServerLevel newLevel, double x, double y, double z, float yaw, float pitch);

    @Shadow
    public abstract void setCamera(@Nullable Entity entityToSpectate);

    @Shadow
    public abstract void resetFallDistance();

    @Shadow
    public abstract boolean canHarmPlayer(Player other);

    @Shadow
    public abstract void setRespawnPosition(ResourceKey<Level> resourceKey, @Nullable BlockPos blockPos, float f, boolean bl, boolean bl2);

    @Shadow
    @Final
    private ContainerSynchronizer containerSynchronizer;
    @Shadow
    private Vec3 enteredNetherPosition;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void neotenet$init(CallbackInfo ci) {
        this.displayName = getScoreboardName();
        this.bukkitPickUpLoot = true;
        this.maxHealthCache = this.getMaxHealth();
        this.neotenet$initialized = true;
    }

    // Use method to resend items in hands in case of client desync, because the item use got cancelled.
    // For example, when cancelling the leash event
    @Override
    public void resendItemInHands() {
        containerMenu.findSlot(getInventory(), getInventory().selected).ifPresent(s -> {
            containerSynchronizer.sendSlotChange(containerMenu, s, getMainHandItem());
        });
        containerSynchronizer.sendSlotChange(inventoryMenu, InventoryMenu.SHIELD_SLOT, getOffhandItem());
    }

    // Yes, this doesn't match Vanilla, but it's the best we can do for now.
    // If this is an issue, PRs are welcome
    @Override
    public final BlockPos getSpawnPoint(ServerLevel worldserver) {
        BlockPos blockposition = worldserver.getSharedSpawnPos();

        if (worldserver.dimensionType().hasSkyLight() && worldserver.serverLevelData.getGameType() != GameType.ADVENTURE) {
            int i = Math.max(0, this.server.getSpawnRadius(worldserver));
            int j = Mth.floor(worldserver.getWorldBorder().getDistanceToBorder((double) blockposition.getX(), (double) blockposition.getZ()));

            if (j < i) {
                i = j;
            }

            if (j <= 1) {
                i = 1;
            }

            long k = (long) (i * 2 + 1);
            long l = k * k;
            int i1 = l > 2147483647L ? Integer.MAX_VALUE : (int) l;
            int j1 = this.getCoprime(i1);
            int k1 = RandomSource.create().nextInt(i1);

            for (int l1 = 0; l1 < i1; ++l1) {
                int i2 = (k1 + j1 * l1) % i1;
                int j2 = i2 % (i * 2 + 1);
                int k2 = i2 / (i * 2 + 1);
                BlockPos blockposition1 = PlayerRespawnLogic.getOverworldRespawnPos(worldserver, blockposition.getX() + j2 - i, blockposition.getZ() + k2 - i);

                if (blockposition1 != null) {
                    return blockposition1;
                }
            }
        }

        return blockposition;
    }
    // CraftBukkit end

    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    private void neotenet$readExtra(CompoundTag compound, CallbackInfo ci) {
        this.getBukkitEntity().readExtraData(compound);
        // CraftBukkit start
        String spawnWorld = compound.getString("SpawnWorld");
        CraftWorld oldWorld = (CraftWorld) Bukkit.getWorld(spawnWorld);
        if (oldWorld != null) {
            this.respawnDimension = oldWorld.getHandle().dimension();
        }
        // CraftBukkit end
    }

    @Redirect(method = "addAdditionalSaveData", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hasExactlyOnePlayerPassenger()Z"))
    private boolean neotenet$nonPersistVehicle(Entity entity) {
        Entity entity1 = this.getVehicle();
        boolean persistVehicle = true;
        if (entity1 != null) {
            Entity vehicle;
            for (vehicle = entity1; vehicle != null; vehicle = vehicle.getVehicle()) {
                if (!vehicle.persist) {
                    persistVehicle = false;
                    break;
                }
            }
        }
        return persistVehicle && entity.hasExactlyOnePlayerPassenger();
    }

    @Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
    private void neotenet$writeExtra(CompoundTag compound, CallbackInfo ci) {
        this.getBukkitEntity().setExtraData(compound);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void neotenet$joining(CallbackInfo ci) {
        if (this.joining) {
            this.joining = false;
        }
    }

    @Redirect(method = "doTick", at = @At(value = "NEW", args = "class=net/minecraft/network/protocol/game/ClientboundSetHealthPacket"))
    private ClientboundSetHealthPacket neotenet$useScaledHealth(float healthIn, int foodLevelIn, float saturationLevelIn) {
        return new ClientboundSetHealthPacket(this.getBukkitEntity().getScaledHealth(), foodLevelIn, saturationLevelIn);
    }

    @Redirect(method = "doTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V"))
    private void neotenet$sendBukkitSetHealth(ServerGamePacketListenerImpl instance, Packet packet) {
        this.connection.send(new ClientboundSetHealthPacket(this.getBukkitEntity().getScaledHealth(), this.foodData.getFoodLevel(), this.foodData.getSaturationLevel()));// CraftBukkit

    }

    @Inject(method = "doTick", at = @At(value = "FIELD", target = "Lnet/minecraft/server/level/ServerPlayer;tickCount:I", opcode = Opcodes.GETFIELD))
    private void neotenet$updateHealthAndExp(CallbackInfo ci) {
        if (this.maxHealthCache != this.getMaxHealth()) {
            this.getBukkitEntity().updateScaledHealth();
        }
        if (this.oldLevel == -1) {
            this.oldLevel = this.experienceLevel;
        }
        if (this.oldLevel != this.experienceLevel) {
            CraftEventFactory.callPlayerLevelChangeEvent(this.getBukkitEntity(), oldLevel, this.experienceLevel);
            this.oldLevel = this.experienceLevel;
        }
        if (this.getBukkitEntity().hasClientWorldBorder()) {
            ((CraftWorldBorder) this.getBukkitEntity().getWorldBorder()).getHandle().tick();
        }
    }

    @Redirect(method = "awardKillScore", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/scores/Scoreboard;forAllObjectives(Lnet/minecraft/world/scores/criteria/ObjectiveCriteria;Lnet/minecraft/world/scores/ScoreHolder;Ljava/util/function/Consumer;)V"))
    private void neotenet$useCustomScoreboard(Scoreboard instance, ObjectiveCriteria criteria, ScoreHolder scoreboardName, Consumer<ScoreAccess> points) {
        this.level().getCraftServer().getScoreboardManager().forAllObjectives(criteria, scoreboardName, points);
    }

    @Redirect(method = "handleTeamKill", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/scores/Scoreboard;forAllObjectives(Lnet/minecraft/world/scores/criteria/ObjectiveCriteria;Lnet/minecraft/world/scores/ScoreHolder;Ljava/util/function/Consumer;)V"))
    private void neotenet$teamKill(Scoreboard instance, ObjectiveCriteria criteria, ScoreHolder scoreboardName, Consumer<ScoreAccess> points) {
        this.level().getCraftServer().getScoreboardManager().forAllObjectives(criteria, scoreboardName, points);
    }

    @Inject(method = "isPvpAllowed", cancellable = true, at = @At("HEAD"))
    private void neotenet$pvpMode(CallbackInfoReturnable<Boolean> cir) {
        // CraftBukkit - this.server.isPvpAllowed() -> this.world.pvpMode
        cir.setReturnValue((this.level().pvpMode));
    }

    @Inject(method = "changeDimension", cancellable = true, at = @At("HEAD"))
    private void neotenet$changeDimension(CallbackInfoReturnable<Boolean> cir) {
        if (this.isSleeping()) cir.setReturnValue(null);// CraftBukkit - SPIGOT-3154
    }

    /*
    @Redirect(method = "changeDimension", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;teleport(DDDFF)V"))
    private void neotenet$changeDimension$teleport(ServerGamePacketListenerImpl instance, double p_9775_, double p_9776_, double p_9777_, float p_9778_, float p_9779_, @Local(argsOnly = true) DimensionTransition p_350472_) {
        this.result = instance.teleport(p_9775_, p_9776_, p_9777_, p_9778_, p_9779_, p_350472_.getTeleportCause());
    }*/

    @Inject(method = "changeDimension", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;teleport(DDDFF)V"), cancellable = true)
    private void neotenet$changeDimension$teleport$if(CallbackInfoReturnable<Entity> cir) {
        if (!this.result) {
            cir.cancel();
        }
    }

    @Inject(method = "changeDimension", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;pop()V", shift = At.Shift.BEFORE), cancellable = true)
    private void neotenet$changeDimension$callPlayerTeleportEvent(DimensionTransition p_350472_, CallbackInfoReturnable<Entity> cir, @Local(ordinal = 1) ServerLevel serverLevel) {
        // CraftBukkit start
        Location enter = this.getBukkitEntity().getLocation();
        Location exit = (serverLevel == null) ? null : CraftLocation.toBukkit(p_350472_.pos(), serverLevel.getWorld(), p_350472_.yRot(), p_350472_.xRot());
        PlayerTeleportEvent tpEvent = new PlayerTeleportEvent(this.getBukkitEntity(), enter, exit, p_350472_.cause());
        Bukkit.getServer().getPluginManager().callEvent(tpEvent);
        if (tpEvent.isCancelled() || tpEvent.getTo() == null) {
            cir.setReturnValue(null);
        }
        exit = tpEvent.getTo();
        this.serverLevel = ((CraftWorld) exit.getWorld()).getHandle();
        this.exit = exit;
        // CraftBukkit end
        this.setServerLevel(serverLevel);
    }

    @Redirect(method = "changeDimension", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;teleport(DDDFF)V"))
    private void neotenet$changeDimension$teleport(ServerGamePacketListenerImpl instance, double p_9775_, double p_9776_, double p_9777_, float p_9778_, float p_9779_, @Local(ordinal = 1) ServerLevel serverLevel1) {
        instance.teleport(exit);// CraftBukkit - use internal teleport without event
    }

    @Inject(method = "changeDimension", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/event/EventHooks;firePlayerChangedDimensionEvent(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/resources/ResourceKey;)V"))
    private void neotenet$changeDimension$beforeReturn(DimensionTransition p_350472_, CallbackInfoReturnable<Entity> cir, @Local(ordinal = 1) ServerLevel serverLevel1) {
        // CraftBukkit start
        PlayerChangedWorldEvent changeEvent = new PlayerChangedWorldEvent(this.getBukkitEntity(), serverLevel1.getWorld());
        this.level().getCraftServer().getPluginManager().callEvent(changeEvent);
        // CraftBukkit end
    }

    // CraftBukkit start
    @Override
    public CraftPortalEvent callPortalEvent(Entity entity, Location exit, PlayerTeleportEvent.TeleportCause cause, int searchRadius, int creationRadius) {
        Location enter = this.getBukkitEntity().getLocation();
        PlayerPortalEvent event = new PlayerPortalEvent(this.getBukkitEntity(), enter, exit, cause, searchRadius, true, creationRadius);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled() || event.getTo() == null || event.getTo().getWorld() == null) {
            return null;
        }
        return new CraftPortalEvent(event);
    }
    // CraftBukkit end

    @Override
    public DimensionTransition findRespawnPositionAndUseSpawnBlock(boolean p_348590_, DimensionTransition.PostDimensionTransition p_352261_, PlayerRespawnEvent.RespawnReason reason) {
        DimensionTransition dimensionTransition;
        boolean isBedSpawn = false;
        boolean isAnchorSpawn = false;
        // CraftBukkit end
        BlockPos blockpos = this.getRespawnPosition();
        float f = this.getRespawnAngle();
        boolean flag = this.isRespawnForced();
        ServerLevel serverlevel = this.server.getLevel(this.getRespawnDimension());
        if (serverlevel != null && blockpos != null) {
            Optional<ServerPlayer.RespawnPosAngle> optional = this.findRespawnAndUseSpawnBlock(serverlevel, blockpos, f, flag, p_348590_);
            if (optional.isPresent()) {
                ServerPlayer.RespawnPosAngle serverplayer$respawnposangle = optional.get();
                // CraftBukkit start
                isBedSpawn = serverplayer$respawnposangle.isBedSpawn();
                isAnchorSpawn = serverplayer$respawnposangle.isAnchorSpawn();
                dimensionTransition = new DimensionTransition(serverlevel, serverplayer$respawnposangle.position(), Vec3.ZERO, serverplayer$respawnposangle.yaw(), 0.0F, p_352261_);
                // CraftBukkit end
            } else {
                dimensionTransition = DimensionTransition.missingRespawnBlock(this.server.overworld(), this, p_352261_); // CraftBukkit
            }
        } else {
            dimensionTransition = new DimensionTransition(this.server.overworld(), this, p_352261_); // CraftBukkit
        }
        // CraftBukkit start
        if (reason == null) {
            return dimensionTransition;
        }
        org.bukkit.entity.Player respawnPlayer = this.getBukkitEntity();
        Location location = CraftLocation.toBukkit(dimensionTransition.pos(), dimensionTransition.newLevel().getWorld(), dimensionTransition.yRot(), dimensionTransition.xRot());

        PlayerRespawnEvent respawnEvent = new PlayerRespawnEvent(respawnPlayer, location, isBedSpawn, isAnchorSpawn, reason);
        this.level().getCraftServer().getPluginManager().callEvent(respawnEvent);

        location = respawnEvent.getRespawnLocation();
        return new DimensionTransition(((CraftWorld) location.getWorld()).getHandle(), CraftLocation.toVec3D(location), dimensionTransition.speed(), location.getYaw(), location.getPitch(), dimensionTransition.missingRespawnBlock(), dimensionTransition.postDimensionTransition(), dimensionTransition.getTeleportCause());
        // CraftBukkit end
    }


    @Redirect(method = "adjustSpawnLocation", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/storage/ServerLevelData;getGameType()Lnet/minecraft/world/level/GameType;"))
    private GameType neotenet$useWorldGameType(ServerLevelData instance, @Local(argsOnly = true) ServerLevel p_352206_) {
        return p_352206_.K.getGameType();
    }

    @Redirect(method = "triggerDimensionChangeTriggers", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/critereon/ChangeDimensionTrigger;trigger(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/resources/ResourceKey;)V"))
    public void neotenet$triggerDimensionChangeTriggers(ChangeDimensionTrigger instance, ServerPlayer p_19758_, ResourceKey<Level> p_19759_, ResourceKey<Level> p_19760_, @Local(argsOnly = true) ServerLevel serverLevel) {
        // CraftBukkit start
        ResourceKey<Level> maindimensionkey = CraftDimensionUtil.getMainDimensionKey(serverLevel);
        ResourceKey<Level> maindimensionkey1 = CraftDimensionUtil.getMainDimensionKey(this.level());
        CriteriaTriggers.CHANGED_DIMENSION.trigger(((ServerPlayer) (Object) this), maindimensionkey, maindimensionkey1);
        if (maindimensionkey != p_19759_ || maindimensionkey1 != p_19760_) {
            CriteriaTriggers.CHANGED_DIMENSION.trigger(((ServerPlayer) (Object) this), p_19759_, p_19760_);
        }
    }

    @Redirect(method = "triggerDimensionChangeTriggers", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/critereon/DistanceTrigger;trigger(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/phys/Vec3;)V"))
    public void neotenet$triggerDimensionChangeTriggers$if(DistanceTrigger instance, ServerPlayer p_186166_, Vec3 p_186167_, @Local(argsOnly = true) ServerLevel serverLevel) {
        // CraftBukkit start
        ResourceKey<Level> maindimensionkey = CraftDimensionUtil.getMainDimensionKey(serverLevel);
        ResourceKey<Level> maindimensionkey1 = CraftDimensionUtil.getMainDimensionKey(this.level());
        if (maindimensionkey == Level.NETHER && maindimensionkey1 == Level.OVERWORLD && this.enteredNetherPosition != null) {
            CriteriaTriggers.NETHER_TRAVEL.trigger(((ServerPlayer) (Object) this), this.enteredNetherPosition);
        }
        if (maindimensionkey1 != Level.NETHER) {
            this.enteredNetherPosition = null;
        }
    }


    @Shadow
    public boolean isRespawnForced() {
        return false;
    }

    @Shadow
    public ResourceKey<Level> getRespawnDimension() {
        return null;
    }

    @Shadow
    public static Optional<ServerPlayer.RespawnPosAngle> findRespawnAndUseSpawnBlock(
            ServerLevel p_348505_, BlockPos p_348607_, float p_348481_, boolean p_348513_, boolean p_348600_
    ) {
        return null;
    }

    @Override
    public void spawnIn(Level world) {
        this.setLevel(world);
        if (world == null) {
            this.unsetRemoved();
            Vec3 position = null;
            if (this.respawnDimension != null) {
                world = this.server.getLevel(this.respawnDimension);
                if (world != null && this.getRespawnPosition() != null) {
                    position = ServerPlayer.findRespawnAndUseSpawnBlock((ServerLevel) world, this.getRespawnPosition(), this.getRespawnAngle(), false, false).map(ServerPlayer.RespawnPosAngle::position).orElse(null);
                }
            }
            if (world == null || position == null) {
                world = ((CraftWorld) Bukkit.getServer().getWorlds().get(0)).getHandle();
                position = Vec3.atCenterOf(world.getSharedSpawnPos());
            }
            this.setLevel(world);
            this.setPos(position);
        }
        this.gameMode.setLevel((ServerLevel) world);
    }

    @Override
    public void resetPlayerWeather() {
        this.weather = null;
        this.setPlayerWeather(this.level().getLevelData().isRaining() ? WeatherType.DOWNFALL : WeatherType.CLEAR, false);
    }

    @Override
    public void updateWeather(float oldRain, float newRain, float oldThunder, float newThunder) {
        if (this.weather == null) {
            if (oldRain != newRain) {
                this.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.RAIN_LEVEL_CHANGE, newRain));
            }
        } else if (this.pluginRainPositionPrevious != this.pluginRainPosition) {
            this.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.RAIN_LEVEL_CHANGE, this.pluginRainPosition));
        }
        if (oldThunder != newThunder) {
            if (this.weather == WeatherType.DOWNFALL || this.weather == null) {
                this.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.THUNDER_LEVEL_CHANGE, newThunder));
            } else {
                this.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.THUNDER_LEVEL_CHANGE, 0.0f));
            }
        }
    }


    @Override
    public void pushRespawnCause(PlayerRespawnEvent.RespawnReason reason) {
        System.out.println("Test Fix");
    }

    @Override
    public void tickWeather() {
        if (this.weather == null) {
            return;
        }
        this.pluginRainPositionPrevious = this.pluginRainPosition;
        if (this.weather == WeatherType.DOWNFALL) {
            this.pluginRainPosition += (float) 0.01;
        } else {
            this.pluginRainPosition -= (float) 0.01;
        }
        this.pluginRainPosition = Mth.clamp(this.pluginRainPosition, 0.0f, 1.0f);
    }

    @Override
    public void forceSetPositionRotation(double x, double y, double z, float yaw, float pitch) {
        this.moveTo(x, y, z, yaw, pitch);
        this.connection.resetPosition();
    }

    @Override
    public void pushChangeSpawnCause(PlayerSpawnChangeEvent.Cause cause) {
        this.neotenet$spawnChangeCause = cause;
    }

    @Override
    public void setRespawnPosition(ResourceKey<Level> level, @Nullable BlockPos pos, float pitch, boolean flag, boolean flag1, PlayerSpawnChangeEvent.Cause cause) {
        neotenet$spawnChangeCause = cause;
        this.setRespawnPosition(level, pos, pitch, flag, flag1);
    }


    @Inject(method = "setRespawnPosition", at = @At("HEAD"))
    private void neotenet$spawnChangeEvent(ResourceKey<Level> resourceKey, BlockPos blockPos, float f, boolean bl, boolean bl2, CallbackInfo ci) {
        var cause = neotenet$spawnChangeCause == null ? PlayerSpawnChangeEvent.Cause.UNKNOWN : neotenet$spawnChangeCause;
        neotenet$spawnChangeCause = null;
        ServerLevel newWorld = this.server.getLevel(blockPos == null ? Level.OVERWORLD : resourceKey);
        Location newSpawn = (blockPos != null) ? CraftLocation.toBukkit(blockPos, newWorld.getWorld(), f, 0) : null;

        PlayerSpawnChangeEvent event = new PlayerSpawnChangeEvent(this.getBukkitEntity(), newSpawn, bl, cause);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        newSpawn = event.getNewSpawn();
        bl = event.isForced();

        if (newSpawn != null) {
            resourceKey = ((CraftWorld) newSpawn.getWorld()).getHandle().dimension();
            blockPos = BlockPos.containing(newSpawn.getX(), newSpawn.getY(), newSpawn.getZ());
            f = newSpawn.getYaw();
        } else {
            resourceKey = Level.OVERWORLD;
            blockPos = null;
            f = 0.0F;
        }
    }

    @Override
    public void setPlayerWeather(WeatherType type, boolean plugin) {
        if (!plugin && this.weather != null) {
            return;
        }
        if (plugin) {
            this.weather = type;
        }
        if (type == WeatherType.DOWNFALL) {
            this.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.STOP_RAINING, 0.0f));
        } else {
            this.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.START_RAINING, 0.0f));
        }
    }

    @Override
    public Either<BedSleepingProblem, Unit> getBedResult(BlockPos blockposition, Direction enumdirection) {
        if (!this.isSleeping() && this.isAlive()) {
            if (!this.level().dimensionType().natural() || !this.level().dimensionType().bedWorks()) {
                return Either.left(BedSleepingProblem.NOT_POSSIBLE_HERE);
            }
            if (!this.bedInRange(blockposition, enumdirection)) {
                return Either.left(BedSleepingProblem.TOO_FAR_AWAY);
            }
            if (this.bedBlocked(blockposition, enumdirection)) {
                return Either.left(BedSleepingProblem.OBSTRUCTED);
            }
            this.setRespawnPosition(this.level().dimension(), blockposition, this.getYRot(), false, true);
            if (this.level().isDay()) {
                return Either.left(BedSleepingProblem.NOT_POSSIBLE_NOW);
            }
            if (!this.isCreative()) {
                double d0 = 8.0;
                double d1 = 5.0;
                Vec3 vec3d = Vec3.atBottomCenterOf(blockposition);
                List<Monster> list = this.level().getEntitiesOfClass(Monster.class, new AABB(vec3d.x() - 8.0, vec3d.y() - 5.0, vec3d.z() - 8.0, vec3d.x() + 8.0, vec3d.y() + 5.0, vec3d.z() + 8.0), entitymonster -> entitymonster.isPreventingPlayerRest((ServerPlayer) (Object) this));
                if (!list.isEmpty()) {
                    return Either.left(BedSleepingProblem.NOT_SAFE);
                }
            }
            return Either.right(Unit.INSTANCE);
        }
        return Either.left(BedSleepingProblem.OTHER_PROBLEM);
    }

    @Override
    public Either<Player.BedSleepingProblem, Unit> startSleepInBed(BlockPos blockposition, boolean force) {
        Direction enumdirection = (Direction) this.level().getBlockState(blockposition).getValue(BlockStateProperties.HORIZONTAL_FACING);
        Either<Player.BedSleepingProblem, Unit> bedResult = this.getBedResult(blockposition, enumdirection);

        if (bedResult.left().orElse(null) == Player.BedSleepingProblem.OTHER_PROBLEM) {
            return bedResult; // return immediately if the result is not bypassable by plugins
        }

        if (force) {
            bedResult = Either.right(Unit.INSTANCE);
        }

        bedResult = org.bukkit.craftbukkit.event.CraftEventFactory.callPlayerBedEnterEvent(this, blockposition, bedResult);
        if (bedResult.left().isPresent()) {
            return bedResult;
        }
        {
            {
                // Start vanilla code
                Either<Player.BedSleepingProblem, Unit> either = super.startSleepInBed(blockposition).ifRight(p_9029_ -> {
                    this.awardStat(Stats.SLEEP_IN_BED);
                    CriteriaTriggers.SLEPT_IN_BED.trigger((ServerPlayer) (Object) this);
                });
                if (!this.serverLevel().canSleepThroughNights()) {
                    this.displayClientMessage(Component.translatable("sleep.not_possible"), true);
                }

                ((ServerLevel) this.level()).updateSleepingPlayerList();
                return either;
            }
        }
    }

    @Override
    public Scoreboard getScoreboard() {
        return this.getBukkitEntity().getScoreboard().getHandle();
    }

    @Override
    public void reset() {
        float exp = 0.0f;
        if (this.keepLevel) {
            exp = this.experienceProgress;
            this.newTotalExp = this.totalExperience;
            this.newLevel = this.experienceLevel;
        }
        this.setHealth(this.getMaxHealth());
        this.stopUsingItem();
        this.setRemainingFireTicks(0);
        this.resetFallDistance();
        this.foodData = new FoodData();
        this.foodData.entityhuman = ((ServerPlayer) (Object) this);
        this.experienceLevel = this.newLevel;
        this.totalExperience = this.newTotalExp;
        this.experienceProgress = 0.0f;
        this.deathTime = 0;
        this.setArrowCount(0, true);
        this.removeAllEffects(EntityPotionEffectEvent.Cause.DEATH);
        this.effectsDirty = true;
        this.containerMenu = this.inventoryMenu;
        this.lastHurtByPlayer = null;
        this.lastHurtByMob = null;
        this.combatTracker = new CombatTracker((ServerPlayer) (Object) this);
        this.lastSentExp = -1;
        if (this.keepLevel) {
            this.experienceProgress = exp;
        } else {
            this.giveExperiencePoints(this.newExp);
        }
        this.keepLevel = false;
        this.setDeltaMovement(0, 0, 0);
    }

    @Inject(method = "checkMovementStatistics", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/server/level/ServerPlayer;causeFoodExhaustion(F)V"))
    private void neotenet$exhauseCause1(double x, double y, double z, CallbackInfo ci) {
        pushExhaustReason(EntityExhaustionEvent.ExhaustionReason.SWIM);
    }

    @Inject(method = "checkMovementStatistics", at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/server/level/ServerPlayer;causeFoodExhaustion(F)V"))
    private void neotenet$exhauseCause2(double x, double y, double z, CallbackInfo ci) {
        pushExhaustReason(EntityExhaustionEvent.ExhaustionReason.WALK_UNDERWATER);
    }

    @Inject(method = "checkMovementStatistics", at = @At(value = "INVOKE", ordinal = 2, target = "Lnet/minecraft/server/level/ServerPlayer;causeFoodExhaustion(F)V"))
    private void neotenet$exhauseCause3(double x, double y, double z, CallbackInfo ci) {
        pushExhaustReason(EntityExhaustionEvent.ExhaustionReason.WALK_ON_WATER);
    }

    @Inject(method = "checkMovementStatistics", at = @At(value = "INVOKE", ordinal = 3, target = "Lnet/minecraft/server/level/ServerPlayer;causeFoodExhaustion(F)V"))
    private void neotenet$exhauseCause4(double x, double y, double z, CallbackInfo ci) {
        pushExhaustReason(EntityExhaustionEvent.ExhaustionReason.SPRINT);
    }

    @Inject(method = "checkMovementStatistics", at = @At(value = "INVOKE", ordinal = 4, target = "Lnet/minecraft/server/level/ServerPlayer;causeFoodExhaustion(F)V"))
    private void neotenet$exhauseCause5(double x, double y, double z, CallbackInfo ci) {
        pushExhaustReason(EntityExhaustionEvent.ExhaustionReason.CROUCH);
    }

    @Inject(method = "checkMovementStatistics", at = @At(value = "INVOKE", ordinal = 5, target = "Lnet/minecraft/server/level/ServerPlayer;causeFoodExhaustion(F)V"))
    private void neotenet$exhauseCause6(double x, double y, double z, CallbackInfo ci) {
        pushExhaustReason(EntityExhaustionEvent.ExhaustionReason.WALK);
    }

    @Inject(method = "setPlayerInput", cancellable = true, at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;setShiftKeyDown(Z)V"))
    private void neotenet$toggleSneak(float strafe, float forward, boolean jumping, boolean sneaking, CallbackInfo ci) {
        if (sneaking != this.isShiftKeyDown()) {
            PlayerToggleSneakEvent event = new PlayerToggleSneakEvent(this.getBukkitEntity(), sneaking);
            Bukkit.getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                ci.cancel();
            }
        }
    }

    @Redirect(method = "restoreFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/stats/ServerRecipeBook;copyOverData(Lnet/minecraft/stats/RecipeBook;)V"))
    private void neotenet$copyOverData(ServerRecipeBook instance, RecipeBook recipeBook) {
    }

    @Redirect(method = "awardStat", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/scores/Scoreboard;forAllObjectives(Lnet/minecraft/world/scores/criteria/ObjectiveCriteria;Lnet/minecraft/world/scores/ScoreHolder;Ljava/util/function/Consumer;)V"))
    private void neotenet$addStats(Scoreboard instance, ObjectiveCriteria criteria, ScoreHolder scoreHolder, Consumer<ScoreAccess> points) {
        this.level().getCraftServer().getScoreboardManager().forAllObjectives(criteria, scoreHolder, points);
    }

    @Redirect(method = "resetStat", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/scores/Scoreboard;forAllObjectives(Lnet/minecraft/world/scores/criteria/ObjectiveCriteria;Lnet/minecraft/world/scores/ScoreHolder;Ljava/util/function/Consumer;)V"))
    private void neotenet$takeStats(Scoreboard instance, ObjectiveCriteria objectiveCriteria, ScoreHolder scoreHolder, Consumer<ScoreAccess> consumer) {
        this.level().getCraftServer().getScoreboardManager().forAllObjectives(objectiveCriteria, scoreHolder, consumer);
    }

    @Redirect(method = "updateScoreForCriteria", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/scores/Scoreboard;forAllObjectives(Lnet/minecraft/world/scores/criteria/ObjectiveCriteria;Lnet/minecraft/world/scores/ScoreHolder;Ljava/util/function/Consumer;)V"))
    private void neotenet$updateStats(Scoreboard instance, ObjectiveCriteria objectiveCriteria, ScoreHolder scoreHolder, Consumer<ScoreAccess> consumer) {
        // CraftBukkit - Use our scores instead
        this.level().getCraftServer().getScoreboardManager().forAllObjectives(objectiveCriteria, scoreHolder,
                consumer);
    }

    @Inject(method = "resetSentInfo", at = @At("HEAD"))
    private void neotenet$setExpUpdate(CallbackInfo ci) {
        this.lastSentExp = -1;
    }

    @Inject(method = "updateOptions", at = @At("HEAD"))
    private void neotenet$settingChange(ClientInformation packetIn, CallbackInfo ci) {
        if (this.getMainArm() != packetIn.mainHand()) {
            PlayerChangedMainHandEvent event = new PlayerChangedMainHandEvent(this.getBukkitEntity(), (this.getMainArm() == HumanoidArm.LEFT) ? MainHand.LEFT : MainHand.RIGHT);
            Bukkit.getPluginManager().callEvent(event);
        }
        if (this.locale == null || !this.locale.equals(packetIn.language())) { // Paper - check for null
            PlayerLocaleChangeEvent event2 = new PlayerLocaleChangeEvent(this.getBukkitEntity(), packetIn.language());
            Bukkit.getPluginManager().callEvent(event2);
        }
        this.locale = packetIn.language();
        this.clientViewDistance = packetIn.viewDistance();
    }

    @Inject(method = "setCamera",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerPlayer;teleportTo(Lnet/minecraft/server/level/ServerLevel;DDDLjava/util/Set;FF)Z"))
    private void neotenet$pushSpectiveTpReason(Entity entity, CallbackInfo ci) {
        this.connection.pushTeleportCause(PlayerTeleportEvent.TeleportCause.SPECTATE);
    }

    @Override
    public CraftPlayer getBukkitEntity() {
        return (CraftPlayer) super.getBukkitEntity();
    }

    @Override
    public boolean isImmobile() {
        return super.isImmobile() || !getBukkitEntity().isOnline();
    }

    @Override
    public String toString() {
        return super.toString() + "(" + this.getScoreboardName() + " at " + this.getX() + "," + this.getY() + "," + this.getZ() + ")";
    }

    @Override
    public int nextContainerCounterInt() {
        this.containerCounter = this.containerCounter % 100 + 1;
        return containerCounter; // CraftBukkit
    }

    @Inject(method = "openHorseInventory", at = @At("HEAD"), cancellable = true)
    private void neotenet$menuEvent(AbstractHorse abstractHorse, Container container, CallbackInfo ci) {
        // CraftBukkit start - Inventory open hook
        this.nextContainerCounterInt();
        AbstractContainerMenu neotenet$container = new HorseInventoryMenu(this.containerCounter, this.getInventory(), container, abstractHorse, abstractHorse.getInventoryColumns());
        neotenet$horseMenu.set((HorseInventoryMenu) neotenet$container);
        neotenet$container.setTitle(abstractHorse.getDisplayName());
        neotenet$container = CraftEventFactory.callInventoryOpenEvent(((ServerPlayer) (Object) this), neotenet$container);
        if (neotenet$container == null) {
            container.stopOpen(this);
            ci.cancel();
        }
    }

    @Redirect(method = "openHorseInventory", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;nextContainerCounter()V"))
    private void neotenet$cancelNext(ServerPlayer instance) {
    }

    @Redirect(method = "openHorseInventory", at = @At(value = "NEW", args = "class=net/minecraft/world/inventory/HorseInventoryMenu"))
    private HorseInventoryMenu neotenet$resetHorseMenu(int i, Inventory inventory, Container container, AbstractHorse abstractHorse, int j) {
        return neotenet$horseMenu.get();
    }

    @Redirect(method = "die", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/scores/Scoreboard;forAllObjectives(Lnet/minecraft/world/scores/criteria/ObjectiveCriteria;Lnet/minecraft/world/scores/ScoreHolder;Ljava/util/function/Consumer;)V"))
    private void neotenet$useBukkitScore(Scoreboard instance, ObjectiveCriteria objectiveCriteria, ScoreHolder scoreHolder, Consumer<ScoreAccess> consumer) {
        this.setCamera(((ServerPlayer) (Object) this)); // Remove spectated target
        // CraftBukkit end
        // CraftBukkit - Get our scores instead
        this.level().getCraftServer().getScoreboardManager().forAllObjectives(ObjectiveCriteria.DEATH_COUNT, scoreHolder, ScoreAccess::increment);
    }

    @ModifyExpressionValue(method = "die", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/GameRules;getBoolean(Lnet/minecraft/world/level/GameRules$Key;)Z"))
    private boolean neotenet$keepInventory(boolean original) {
        return original || this.isSpectator();
    }

    @Inject(method = "die", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/GameRules;getBoolean(Lnet/minecraft/world/level/GameRules$Key;)Z"))
    private void neotenet$checkIfRemoved(DamageSource p_9035_, CallbackInfo ci) {
        // CraftBukkit start - fire PlayerDeathEvent
        if (this.isRemoved()) {
            ci.cancel();
            return;
        }
    }

    @Inject(method = "die", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/CombatTracker;getDeathMessage()Lnet/minecraft/network/chat/Component;"), cancellable = true)
    private void neotenet$fireDeathEvent(DamageSource p_9035_, CallbackInfo ci, @Local boolean flag, @Share("loot") LocalRef<java.util.List<org.bukkit.inventory.ItemStack>> loot, @Share("neotenet$flag") LocalBooleanRef neotenet$flag) {
        neotenet$flag.set(flag);
        loot.set(new java.util.ArrayList<>(this.getInventory().getContainerSize()));
        for (ItemStack item : this.getInventory().getContents()) {
            if (!item.isEmpty() && !EnchantmentHelper.has(item, EnchantmentEffectComponents.PREVENT_EQUIPMENT_DROP)) {
                loot.get().add(CraftItemStack.asCraftMirror(item).markForInventoryDrop());
            }
        }
    }

    @Inject(method = "die", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;removeEntitiesOnShoulder()V"))
    private void neotenet$addLoots(DamageSource p_9035_, CallbackInfo ci, @Share("loot") LocalRef<java.util.List<org.bukkit.inventory.ItemStack>> loot, @Share("neotenet$flag") LocalBooleanRef neotenet$flag) {
        // SPIGOT-5071: manually add player loot tables (SPIGOT-5195 - ignores keepInventory rule)
        this.dropFromLootTable(p_9035_, this.lastHurtByPlayerTime > 0);
        this.dropCustomDeathLoot(this.serverLevel(), p_9035_, neotenet$flag.get());

        loot.get().addAll(this.drops);
        this.drops.clear(); // SPIGOT-5188: make sure to clear
    }

    @Redirect(method = "die", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;isSpectator()Z"))
    private boolean netoent$isSpectator(ServerPlayer instance, @Local(argsOnly = true) DamageSource p_9035_) {
        // SPIGOT-5478 must be called manually now
        this.dropExperience(p_9035_.getEntity());
        // we clean the player's inventory after the EntityDeathEvent is called so plugins can get the exact state of the inventory.
        return event.getKeepInventory();
    }

    @Redirect(method = "die", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;dropAllDeathLoot(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;)V"))
    private void netoent$dropAllDeathLoot(ServerPlayer instance, ServerLevel serverLevel, DamageSource damageSource, @Local(argsOnly = true) DamageSource p_9035_) {
        this.getInventory().clearContent();
    }

    @Redirect(method = "die", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/CombatTracker;getDeathMessage()Lnet/minecraft/network/chat/Component;"))
    private Component neotenet$callDeathEvent(CombatTracker instance, @Local(argsOnly = true) DamageSource
            p_9035_, @Share("loot") LocalRef<java.util.List<org.bukkit.inventory.ItemStack>> loot, @Share("neotenet$flag") LocalBooleanRef
                                                      neotenet$flag) {
        Component defaultMessage = this.getCombatTracker().getDeathMessage();
        String deathmessage = defaultMessage.getString();
        keepLevel = neotenet$flag.get(); // SPIGOT-2222: pre-set keepLevel
        org.bukkit.event.entity.PlayerDeathEvent event = CraftEventFactory.callPlayerDeathEvent(((ServerPlayer) (Object) this), p_9035_, loot.get(), deathmessage, neotenet$flag.get());
        this.event = event;
        // SPIGOT-943 - only call if they have an inventory open
        if (this.containerMenu != this.inventoryMenu) {
            this.closeContainer();
        }
        if (!event.getKeepInventory()) {
            this.getInventory().clearContent();
        }

        String deathMessage = event.getDeathMessage();
        Component ichatbasecomponent;

        if (deathMessage != null && deathMessage.length() > 0 && neotenet$flag.get()) { // TODO: allow plugins to override?
            if (deathMessage.equals(deathmessage)) {
                ichatbasecomponent = this.getCombatTracker().getDeathMessage();
            } else {
                ichatbasecomponent = org.bukkit.craftbukkit.util.CraftChatMessage.fromStringOrNull(deathMessage);
            }
            return ichatbasecomponent;
        }
        return defaultMessage;
    }

    @Redirect(method = "openMenu(Lnet/minecraft/world/MenuProvider;Ljava/util/function/Consumer;)Ljava/util/OptionalInt;", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;closeContainer()V", ordinal = 0))
    private void neotenet$cancelOpenMenu(ServerPlayer instance) {}

    @Definition(id = "abstractcontainermenu", local = @Local(type = AbstractContainerMenu.class, ordinal = 0))
    @Expression("abstractcontainermenu == null")
    @Inject(method = "openMenu(Lnet/minecraft/world/MenuProvider;Ljava/util/function/Consumer;)Ljava/util/OptionalInt;", at = @At("MIXINEXTRAS:EXPRESSION"), cancellable = true)
    private void neotenet$callInventoryOpenEvent(MenuProvider p_9033_, Consumer<RegistryFriendlyByteBuf> extraDataWriter, CallbackInfoReturnable<OptionalInt> cir, @Local AbstractContainerMenu abstractcontainermenu) {
        // CraftBukkit start - Inventory open hook
        if (abstractcontainermenu != null) {
            abstractcontainermenu.setTitle(p_9033_.getDisplayName());

            boolean cancelled = false;
            abstractcontainermenu = CraftEventFactory.callInventoryOpenEvent(((ServerPlayer) (Object) this), abstractcontainermenu, cancelled);
            if (abstractcontainermenu == null && !cancelled) { // Let pre-cancelled events fall through
                // SPIGOT-5263 - close chest if cancelled
                if (p_9033_ instanceof Container) {
                    ((Container) p_9033_).stopOpen(this);
                } else if (p_9033_ instanceof ChestBlock.DoubleInventory) {
                    // SPIGOT-5355 - double chests too :(
                    ((ChestBlock.DoubleInventory) p_9033_).inventorylargechest.stopOpen(this);
                }
                cir.setReturnValue(OptionalInt.empty());
            }
        }
        // CraftBukkit end
    }

    @ModifyArg(method = "openMenu(Lnet/minecraft/world/MenuProvider;Ljava/util/function/Consumer;)Ljava/util/OptionalInt;", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/game/ClientboundOpenScreenPacket;<init>(ILnet/minecraft/world/inventory/MenuType;Lnet/minecraft/network/chat/Component;)V"), index = 2)
    private Component neotenet$useTitle(Component p_132618_, @Local AbstractContainerMenu abstractcontainermenu) {
       return abstractcontainermenu.getTitle();
    }

    @Override
    public Entity changeDimension(ServerLevel worldserver, PlayerTeleportEvent.TeleportCause cause) {
        neotenet$changeDimensionCause.set(cause);
        DimensionTransition dimensionTransition = this.portalProcess.getPortalDestination(worldserver, this);
        return changeDimension(dimensionTransition);
    }

    @Inject(method = "teleportTo(Lnet/minecraft/server/level/ServerLevel;DDDLjava/util/Set;FF)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;teleport(DDDFFLjava/util/Set;)V"))
    private void neotenet$forwardReason(ServerLevel level, double x, double y, double z, Set<
            RelativeMovement> relativeMovements, float yRot, float xRot, CallbackInfoReturnable<Boolean> cir) {
        this.connection.pushTeleportCause(neotenet$changeDimensionCause.getAndSet(PlayerTeleportEvent.TeleportCause.UNKNOWN));
    }

    @Inject(method = "teleportTo(Lnet/minecraft/server/level/ServerLevel;DDDFF)V", cancellable = true, at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/server/level/ServerPlayer;stopRiding()V"))
    private void neotenet$handleBy(ServerLevel world, double d0, double d1, double d2, float f,
                                   float f1, CallbackInfo ci) {
        this.getBukkitEntity().teleport(new Location(world.getWorld(), d0, d1, d2, f, f1), neotenet$changeDimensionCause.getAndSet(PlayerTeleportEvent.TeleportCause.UNKNOWN));
        ci.cancel();
    }

    @Override
    public void teleportTo(ServerLevel worldserver, double d0, double d1, double d2, float f,
                           float f1, PlayerTeleportEvent.TeleportCause cause) {
        pushChangeDimensionCause(cause);
        teleportTo(worldserver, d0, d1, d2, f, f1);
    }

    @Override
    public boolean teleportTo(ServerLevel worldserver, double d0, double d1, double d2, Set<
            RelativeMovement> pRelativeMovements, float f, float f1, PlayerTeleportEvent.TeleportCause cause) {
        pushChangeDimensionCause(cause);
        return teleportTo(worldserver, d0, d1, d2, pRelativeMovements, f, f1);
    }

    @Inject(method = "stopSleepInBed",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;teleport(DDDFF)V"))
    private void neotenet$tpCauseExitBed(boolean wakeImmediately,
                                         boolean updateLevelForSleepingPlayers, CallbackInfo ci) {
        this.connection.pushTeleportCause(PlayerTeleportEvent.TeleportCause.EXIT_BED);
    }

    @Inject(method = "stopSleepInBed", at = @At("HEAD"), cancellable = true)
    private void neotenet$exitBedEvent(boolean flag, boolean flag1, CallbackInfo ci) {
        if (!this.isSleeping()) ci.cancel(); // CraftBukkit - Can't leave bed if not in one!
        // CraftBukkit start - fire PlayerBedLeaveEvent
        CraftPlayer player = this.getBukkitEntity();
        BlockPos bedPosition = this.getSleepingPos().orElse(null);

        org.bukkit.block.Block bed;
        if (bedPosition != null) {
            bed = this.level().getWorld().getBlockAt(bedPosition.getX(), bedPosition.getY(), bedPosition.getZ());
        } else {
            bed = this.level().getWorld().getBlockAt(player.getLocation());
        }

        PlayerBedLeaveEvent event = new PlayerBedLeaveEvent(player, bed, true);
        this.level().getCraftServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
        // CraftBukkit end
    }

    @Override
    public void pushChangeDimensionCause(PlayerTeleportEvent.TeleportCause cause) {
        neotenet$changeDimensionCause.set(cause);
    }

    /**
     * @author wdog5
     * @reason bukkit
     */
    @Overwrite
    @Nullable
    public Component getTabListDisplayName() {
        return listName; // CraftBukkit
    }

    @Override
    public long getPlayerTime() {
        if (this.relativeTime) {
            return this.level().getDayTime() + this.timeOffset;
        }
        return this.level().getDayTime() - this.level().getDayTime() % 24000L + this.timeOffset;
    }

    @Override
    public CraftPortalEvent callPortalEvent(Entity entity, ServerLevel exitWorldServer, Vec3
            exitPosition, PlayerTeleportEvent.TeleportCause cause, int searchRadius, int creationRadius) {
        Location enter = this.getBukkitEntity().getLocation();
        Location exit = new Location(exitWorldServer.getWorld(), exitPosition.x(), exitPosition.y(), exitPosition.z(), this.getYRot(), this.getXRot());
        PlayerPortalEvent event = new PlayerPortalEvent(this.getBukkitEntity(), enter, exit, cause, 128, true, creationRadius);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled() || event.getTo() == null || event.getTo().getWorld() == null) {
            return null;
        }
        return new CraftPortalEvent(event);
    }

    @Override
    public WeatherType getPlayerWeather() {
        return this.weather;
    }

    // Banner TODO fix mixins
    @Override
    public Optional<BlockUtil.FoundRectangle> getExitPortal(ServerLevel worldserver, BlockPos blockposition,
                                                            boolean flag, WorldBorder worldborder, int searchRadius, boolean canCreatePortal, int createRadius) {
        /*
        Optional<BlockUtil.FoundRectangle> optional = super.getExitPortal(worldserver, blockposition, flag, worldborder);
        if (optional.isPresent() || !canCreatePortal) {
            return optional;
        }
        Direction.Axis enumdirection_enumaxis = this.level().getBlockState(this.portalEntrancePos).getOptionalValue(NetherPortalBlock.AXIS).orElse(Direction.Axis.X);
        Optional<BlockUtil.FoundRectangle> optional1 =  worldserver.getPortalForcer().createPortal(blockposition, enumdirection_enumaxis, (ServerPlayer) (Object) this, createRadius);
        if (!optional1.isPresent()) {
            //  LOGGER.error("Unable to create a portal, likely target out of worldborder");
        }
        return optional1;*/
        return Optional.empty();
    }
}
