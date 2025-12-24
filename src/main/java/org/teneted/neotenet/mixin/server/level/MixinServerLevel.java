package org.teneted.neotenet.mixin.server.level;

import com.google.common.collect.Lists;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderCenterPacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderLerpSizePacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderSizePacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderWarningDelayPacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderWarningDistancePacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.Container;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.BorderChangeListener;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.WorldData;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.block.CraftBlockState;
import org.bukkit.craftbukkit.block.CraftBlockStates;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.generator.CustomChunkGenerator;
import org.bukkit.craftbukkit.generator.CustomWorldChunkManager;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.craftbukkit.util.WorldUUID;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LightningStrike;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.event.world.GenericGameEvent;
import org.bukkit.event.world.TimeSkipEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.objectweb.asm.Opcodes;
import org.spigotmc.SpigotWorldConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.teneted.neotenet.bukkit.DistValidate;
import org.teneted.neotenet.injection.server.level.InjectionServerLevel;
import org.teneted.neotenet.neoforge.BukkitRegistry;
import org.teneted.neotenet.neoforge.NeoTenetDerivedWorldInfo;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

@Mixin(ServerLevel.class)
public abstract class MixinServerLevel extends Level implements WorldGenLevel, InjectionServerLevel {

    @Shadow
    @Final
    private ServerChunkCache chunkSource;

    @Shadow
    public LevelStorageSource.LevelStorageAccess convertable;

    @Shadow
    public UUID uuid;

    @Shadow
    public PrimaryLevelData K;

    @Shadow
    protected abstract boolean sendParticles(ServerPlayer p_8637_, boolean p_8638_, double p_8639_, double p_8640_, double p_8641_, Packet<?> p_8642_);

    @Shadow
    @Final
    public ServerLevelData serverLevelData;

    @Shadow
    @Final
    public PersistentEntitySectionManager<Entity> entityManager;

    @Shadow
    protected abstract void wakeUpAllPlayers();

    @Shadow
    public abstract <T extends ParticleOptions> int sendParticles(T p_8768_, double p_8769_, double p_8770_, double p_8771_, int p_8772_, double p_8773_, double p_8774_, double p_8775_, double p_8776_);

    @Shadow
    protected abstract boolean addEntity(Entity p_8873_);

    @Shadow
    public abstract boolean addWithUUID(Entity p_8848_);

    @Shadow
    public abstract void addDuringTeleport(Entity p_143335_);

    @Shadow
    public ResourceKey<LevelStem> typeKey;

    @Shadow
    @Final
    private MinecraftServer server;

    protected MixinServerLevel(WritableLevelData p_270739_, ResourceKey<Level> p_270683_, RegistryAccess p_270200_, Holder<DimensionType> p_270240_, Supplier<ProfilerFiller> p_270692_, boolean p_270904_, boolean p_270470_, long p_270248_, int p_270466_) {
        super(p_270739_, p_270683_, p_270200_, p_270240_, p_270692_, p_270904_, p_270470_, p_270248_, p_270466_);
    }

    @Override
    public LevelChunk getChunkIfLoaded(int x, int z) {
        return this.chunkSource.getChunk(x, z, false);
    }
    @Override
    public ResourceKey<LevelStem> getTypeKey() {
        return convertable.dimensionType;
    }

    @Inject(method = "<init>(Lnet/minecraft/server/MinecraftServer;Ljava/util/concurrent/Executor;Lnet/minecraft/world/level/storage/LevelStorageSource$LevelStorageAccess;Lnet/minecraft/world/level/storage/ServerLevelData;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/world/level/dimension/LevelStem;Lnet/minecraft/server/level/progress/ChunkProgressListener;ZJLjava/util/List;ZLnet/minecraft/world/RandomSequences;)V", at = @At(value = "FIELD", target = "Lnet/minecraft/server/level/ServerLevel;tickTime:Z", opcode = Opcodes.PUTFIELD))
    private void neotenet$init(MinecraftServer p_214999_, Executor p_215000_, LevelStorageSource.LevelStorageAccess p_215001_, ServerLevelData p_215002_, ResourceKey p_215003_, LevelStem p_215004_, ChunkProgressListener p_215005_, boolean p_215006_, long p_215007_, List p_215008_, boolean p_215009_, RandomSequences p_288977_, CallbackInfo ci) {
        this.pvpMode = p_214999_.isPvpAllowed();
        convertable = p_215001_;
        this.uuid = WorldUUID.getUUID(p_215001_.getDimensionPath(this.dimension()).toFile());
        var typeKey = p_215001_.dimensionType;
        if (typeKey != null) {
            this.typeKey = typeKey;
        } else {
            var dimensions = p_214999_.registryAccess().registryOrThrow(Registries.LEVEL_STEM);
            var key = dimensions.getResourceKey(p_215004_);
            if (key.isPresent()) {
                this.typeKey = key.get();
            } else {
                this.typeKey = ResourceKey.create(Registries.LEVEL_STEM, this.dimension().location());
            }
            if (p_215002_ instanceof DerivedLevelData data) {
                data.setDimType(this.getTypeKey());
            }
        }
    }

    @Inject(method = "<init>(Lnet/minecraft/server/MinecraftServer;Ljava/util/concurrent/Executor;Lnet/minecraft/world/level/storage/LevelStorageSource$LevelStorageAccess;Lnet/minecraft/world/level/storage/ServerLevelData;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/world/level/dimension/LevelStem;Lnet/minecraft/server/level/progress/ChunkProgressListener;ZJLjava/util/List;ZLnet/minecraft/world/RandomSequences;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/dimension/LevelStem;generator()Lnet/minecraft/world/level/chunk/ChunkGenerator;"))
    private void neotenet$setK(MinecraftServer p_214999_, Executor p_215000_, LevelStorageSource.LevelStorageAccess p_215001_, ServerLevelData p_215002_, ResourceKey p_215003_, LevelStem p_215004_, ChunkProgressListener p_215005_, boolean p_215006_, long p_215007_, List p_215008_, boolean p_215009_, RandomSequences p_288977_, CallbackInfo ci) {
        if (p_215002_ instanceof PrimaryLevelData) {
            this.K = (PrimaryLevelData) p_215002_;
        } else {
            this.K = NeoTenetDerivedWorldInfo.create(p_215002_);
        }
    }

    @Inject(method = "<init>(Lnet/minecraft/server/MinecraftServer;Ljava/util/concurrent/Executor;Lnet/minecraft/world/level/storage/LevelStorageSource$LevelStorageAccess;Lnet/minecraft/world/level/storage/ServerLevelData;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/world/level/dimension/LevelStem;Lnet/minecraft/server/level/progress/ChunkProgressListener;ZJLjava/util/List;ZLnet/minecraft/world/RandomSequences;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;forceSynchronousWrites()Z"))
    private void neotenet$initK(MinecraftServer p_214999_, Executor p_215000_, LevelStorageSource.LevelStorageAccess p_215001_, ServerLevelData p_215002_, ResourceKey p_215003_, LevelStem p_215004_, ChunkProgressListener p_215005_, boolean p_215006_, long p_215007_, List p_215008_, boolean p_215009_, RandomSequences p_288977_, CallbackInfo ci, @Local ChunkGenerator chunkgenerator) {
        if (environment == null) {
            environment = BukkitRegistry.environment.get(getTypeKey());
        }
        if (biomeProvider != null) {
            BiomeSource worldChunkManager = new CustomWorldChunkManager(getWorld(), biomeProvider, server.registryAccess().registryOrThrow(Registries.BIOME));
            if (chunkgenerator instanceof NoiseBasedChunkGenerator cga) {
                chunkgenerator = new NoiseBasedChunkGenerator(worldChunkManager, cga.settings);
            } else if (chunkgenerator instanceof FlatLevelSource cpf) {
                chunkgenerator = new FlatLevelSource(cpf.settings(), worldChunkManager);
            }
        } else {
            biomeProvider = getCraftServer().getBiomeProvider(p_215002_.getLevelName());
        }

        if (generator != null) {
            chunkgenerator = new CustomChunkGenerator(((ServerLevel) (Object) this), chunkgenerator, generator);
        } else {
            generator = getCraftServer().getGenerator(p_215002_.getLevelName());
        }
        this.spigotConfig = new SpigotWorldConfig(p_215002_.getLevelName()); // Spigot
    }

    @Redirect(method = "<init>(Lnet/minecraft/server/MinecraftServer;Ljava/util/concurrent/Executor;Lnet/minecraft/world/level/storage/LevelStorageSource$LevelStorageAccess;Lnet/minecraft/world/level/storage/ServerLevelData;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/world/level/dimension/LevelStem;Lnet/minecraft/server/level/progress/ChunkProgressListener;ZJLjava/util/List;ZLnet/minecraft/world/RandomSequences;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;getWorldData()Lnet/minecraft/world/level/storage/WorldData;"))
    private WorldData neotenet$useRespective(MinecraftServer server) {
        return K;
    }

    @Inject(method = "<init>(Lnet/minecraft/server/MinecraftServer;Ljava/util/concurrent/Executor;Lnet/minecraft/world/level/storage/LevelStorageSource$LevelStorageAccess;Lnet/minecraft/world/level/storage/ServerLevelData;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/world/level/dimension/LevelStem;Lnet/minecraft/server/level/progress/ChunkProgressListener;ZJLjava/util/List;ZLnet/minecraft/world/RandomSequences;)V", at = @At("RETURN"))
    private void neotenet$finalInitWorld(MinecraftServer p_214999_, Executor p_215000_, LevelStorageSource.LevelStorageAccess p_215001_, ServerLevelData p_215002_, ResourceKey p_215003_, LevelStem p_215004_, ChunkProgressListener p_215005_, boolean p_215006_, long p_215007_, List p_215008_, boolean p_215009_, RandomSequences p_288977_, CallbackInfo ci) {
        this.world = new CraftWorld(((ServerLevel) (Object) this), generator, biomeProvider, environment);
        this.getCraftServer().addWorld(this.getWorld()); // CraftBukkit
        this.K.setWorld(((ServerLevel) (Object) this));
        // From PlayerList.setPlayerFileData
        getWorldBorder().addListener(new BorderChangeListener() {
            @Override
            public void onBorderSizeSet(WorldBorder worldborder, double d0) {
                getCraftServer().getHandle().broadcastAll(new ClientboundSetBorderSizePacket(worldborder), worldborder.world);
            }

            @Override
            public void onBorderSizeLerping(WorldBorder worldborder, double d0, double d1, long i) {
                getCraftServer().getHandle().broadcastAll(new ClientboundSetBorderLerpSizePacket(worldborder), worldborder.world);
            }

            @Override
            public void onBorderCenterSet(WorldBorder worldborder, double d0, double d1) {
                getCraftServer().getHandle().broadcastAll(new ClientboundSetBorderCenterPacket(worldborder), worldborder.world);
            }

            @Override
            public void onBorderSetWarningTime(WorldBorder worldborder, int i) {
                getCraftServer().getHandle().broadcastAll(new ClientboundSetBorderWarningDelayPacket(worldborder), worldborder.world);
            }

            @Override
            public void onBorderSetWarningBlocks(WorldBorder worldborder, int i) {
                getCraftServer().getHandle().broadcastAll(new ClientboundSetBorderWarningDistancePacket(worldborder), worldborder.world);
            }

            @Override
            public void onBorderSetDamagePerBlock(WorldBorder worldborder, double d0) {
            }

            @Override
            public void onBorderSetDamageSafeZOne(WorldBorder worldborder, double d0) {
            }
        });
        // CraftBukkit end
    }

    @Inject(method = "gameEvent", cancellable = true, at = @At("HEAD"))
    private void neotenet$gameEventEvent(Holder<GameEvent> holder, Vec3 p_215042_, GameEvent.Context context, CallbackInfo ci) {
        var entity = context.sourceEntity();
        var i = holder.value().notificationRadius();
        GenericGameEvent event = new GenericGameEvent(org.bukkit.GameEvent.getByKey(CraftNamespacedKey.fromMinecraft(BuiltInRegistries.GAME_EVENT.getKey(holder.value()))), new Location(this.getWorld(), p_215042_.x(), p_215042_.y(), p_215042_.z()), (entity == null) ? null : entity.getBukkitEntity(), i, !Bukkit.isPrimaryThread());
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Override
    public <T extends ParticleOptions> int sendParticles(ServerPlayer sender, T t0, double d0, double d1, double d2, int i, double d3, double d4, double d5, double d6, boolean force) {
        ClientboundLevelParticlesPacket packet = new ClientboundLevelParticlesPacket(t0, force, d0, d1, d2, (float) d3, (float) d4, (float) d5, (float) d6, i);
        int j = 0;
        for (Player entity : this.players()) {
            if (entity instanceof ServerPlayer serverPlayer) {
                if (sender == null || serverPlayer.getBukkitEntity().canSee(sender.getBukkitEntity())) {
                    if (this.sendParticles(serverPlayer, force, d0, d1, d2, packet)) {
                        ++j;
                    }
                }
            }
        }
        return j;
    }

    @Inject(method = "tickNonPassenger", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/world/entity/Entity;tick()V"))
    private void neotenet$tickPortal(Entity entityIn, CallbackInfo ci) {
        entityIn.postTick();
    }

    @Inject(method = "tickPassenger", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/world/entity/Entity;rideTick()V"))
    private void neotenet$tickPortalPassenger(Entity ridingEntity, Entity passengerEntity, CallbackInfo ci) {
        passengerEntity.postTick();
    }

    @Inject(method = "tickChunk", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    public void neotenet$thunder(LevelChunk chunkIn, int randomTickSpeed, CallbackInfo ci) {
        pushAddEntityReason(CreatureSpawnEvent.SpawnReason.LIGHTNING);
    }

    @Redirect(method = "tickChunk", at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean neotenet$thunder(ServerLevel serverWorld, Entity entityIn) {
        return strikeLightning(entityIn, LightningStrikeEvent.Cause.WEATHER);
    }

    @Override
    public boolean strikeLightning(Entity entity) {
        return this.strikeLightning(entity, LightningStrikeEvent.Cause.UNKNOWN);
    }

    private transient LightningStrikeEvent.Cause neotenet$cause;
    private final AtomicReference<CreatureSpawnEvent.SpawnReason> neotenet$reason = new AtomicReference<>();
    private final AtomicReference<Boolean> neotenet$timeSkipCancelled = new AtomicReference<>(false);
    private transient boolean neotenet$force;
    public AtomicBoolean canaddFreshEntity = new AtomicBoolean(false);

    @Override
    public boolean strikeLightning(Entity entity, LightningStrikeEvent.Cause cause) {
        if (neotenet$cause != null) {
            cause = neotenet$cause;
            neotenet$cause = null;
        }

        // NeoTaiyitist start - Compat for Modded Weather,ignore modded weather effect
        if (entity.getBukkitEntity() instanceof org.bukkit.entity.LightningStrike) {
            LightningStrikeEvent lightning = CraftEventFactory.callLightningStrikeEvent((LightningStrike) entity.getBukkitEntity(), cause);
            if (lightning.isCancelled()) {
                return false;
            }
            // NeoTaiyitist end
        }
        return this.addFreshEntity(entity);
    }

    @Inject(method = "tickPrecipitation", cancellable = true, at = @At(value = "INVOKE", ordinal = 0, shift = At.Shift.BEFORE, target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    public void neotenet$snowForm0(BlockPos blockPos, CallbackInfo ci, @Local(ordinal = 1) BlockPos blockPos2) {

        CraftBlockState craftBlockState = CraftBlockStates.getBlockState((ServerLevel) (Object) this, blockPos2, 3);
        craftBlockState.setData(Blocks.ICE.defaultBlockState());

        BlockFormEvent event = new BlockFormEvent(craftBlockState.getBlock(), craftBlockState);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "tickPrecipitation", cancellable = true, at = @At(value = "INVOKE", ordinal = 1, shift = At.Shift.BEFORE, target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    public void neotenet$snowForm1(BlockPos blockPos, CallbackInfo ci, @Local(ordinal = 1) BlockState blockState2) {

        CraftBlockState craftBlockState = CraftBlockStates.getBlockState((ServerLevel) (Object) this, blockPos, 3);
        craftBlockState.setData(blockState2);

        BlockFormEvent event = new BlockFormEvent(craftBlockState.getBlock(), craftBlockState);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "tickPrecipitation", cancellable = true, at = @At(value = "INVOKE", ordinal = 2, shift = At.Shift.BEFORE, target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    public void neotenet$snowForm2(BlockPos blockPos, CallbackInfo ci) {

        CraftBlockState craftBlockState = CraftBlockStates.getBlockState((ServerLevel) (Object) this, blockPos, 3);
        craftBlockState.setData(Blocks.SNOW.defaultBlockState());

        BlockFormEvent event = new BlockFormEvent(craftBlockState.getBlock(), craftBlockState);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "save", at = @At(value = "JUMP", ordinal = 0, opcode = Opcodes.IFNULL))
    private void neotenet$worldSaveEvent(ProgressListener progress, boolean flush, boolean skipSave, CallbackInfo ci) {
        Bukkit.getPluginManager().callEvent(new WorldSaveEvent(getWorld()));
    }

    @Inject(method = "save", at = @At("TAIL"))
    private void neotenet$saveAllChunks(ProgressListener progress, boolean flush, boolean skipSave, CallbackInfo ci) {
        // CraftBukkit start - moved from MinecraftServer.saveAllChunks
        if (this.serverLevelData instanceof PrimaryLevelData worldInfo) {
            worldInfo.setWorldBorder(this.getWorldBorder().createSettings());
            worldInfo.setCustomBossEvents(this.getServer().getCustomBossEvents().save(this.registryAccess()));
            this.convertable.saveDataTag(this.getServer().registryAccess(), worldInfo, this.getServer().getPlayerList().getSingleplayerData());
        }
        // CraftBukkit end
    }

    @Inject(method = "unload", at = @At("HEAD"))
    public void neotenet$closeOnChunkUnloading(LevelChunk chunkIn, CallbackInfo ci) {
        for (BlockEntity tileentity : chunkIn.getBlockEntities().values()) {
            if (tileentity instanceof Container) {
                for (HumanEntity h : Lists.newArrayList(((Container) tileentity).getViewers())) {
                    if (h instanceof CraftHumanEntity) {
                        ((CraftHumanEntity) h).getHandle().closeContainer();
                    }
                }
            }
        }
    }

    @Redirect(method = "sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/server/level/ServerPlayer;ZDDDLnet/minecraft/network/protocol/Packet;)Z"))
    public boolean neotenet$particleVisible(ServerLevel serverWorld, ServerPlayer player, boolean longDistance, double posX, double posY, double posZ, Packet<?> packet) {
        return this.sendParticles(player, neotenet$force, posX, posY, posZ, packet);
    }

    @Override
    public <T extends ParticleOptions> int sendParticles(T type, double posX, double posY, double posZ, int particleCount, double xOffset, double yOffset, double zOffset, double speed, boolean force) {
        neotenet$force = force;
        return this.sendParticles(type, posX, posY, posZ, particleCount, xOffset, yOffset, zOffset, speed);
    }

    @Inject(method = "addEntity", cancellable = true, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/entity/PersistentEntitySectionManager;addNewEntity(Lnet/minecraft/world/level/entity/EntityAccess;)Z"))
    private void neotenet$addEntityEvent(Entity entityIn, CallbackInfoReturnable<Boolean> cir) {
        CreatureSpawnEvent.SpawnReason reason = neotenet$reason.get() == null ? CreatureSpawnEvent.SpawnReason.DEFAULT : neotenet$reason.get();
        neotenet$reason.set(null);
        if (DistValidate.isValid(this) && !CraftEventFactory.doEntityAddEventCalling((ServerLevel) (Object) this, entityIn, reason)) {
            cir.setReturnValue(false);
        }
    }

    @Override
    public boolean canAddFreshEntity() {
        return canaddFreshEntity.getAndSet(false);
    }

    @Redirect(method = "addFreshEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean neotenet$fixAddFreshEntity(ServerLevel instance, Entity entity) {
        boolean add = addEntity(entity);
        canaddFreshEntity.set(add);
        return add;
    }

    @Override
    public boolean addFreshEntity(Entity entity, CreatureSpawnEvent.SpawnReason reason) {
        pushAddEntityReason(reason);
        return addFreshEntity(entity);
    }

    @Inject(method = "addEntity", at = @At("RETURN"))
    public void neotenet$resetReason(Entity entityIn, CallbackInfoReturnable<Boolean> cir) {
        neotenet$reason.set(null);
    }

    @Override
    public boolean addWithUUID(Entity entity, CreatureSpawnEvent.SpawnReason reason) {
        pushAddEntityReason(reason);
        return this.addWithUUID(entity);
    }

    @Override
    public void addDuringTeleport(Entity entity, CreatureSpawnEvent.SpawnReason reason) {
        pushAddEntityReason(reason);
        addDuringTeleport(entity);
    }

    @Override
    public boolean tryAddFreshEntityWithPassengers(Entity entity, CreatureSpawnEvent.SpawnReason reason) {
        if (entity.getSelfAndPassengers().map(Entity::getUUID).anyMatch(this.entityManager::isLoaded)) {
            return false;
        } else {
            pushAddEntityReason(reason);
            return this.addAllEntities(entity, reason);
        }
    }

    @Override
    public boolean addEntity(Entity entity, CreatureSpawnEvent.SpawnReason reason) {
        return addFreshEntity(entity, reason);
    }

    @Inject(method = "blockUpdated", cancellable = true, at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;updateNeighborsAt(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;)V"))
    private void neotenet$returnIfPopulate(BlockPos pos, Block block, CallbackInfo ci) {
        if (populating) {
            ci.cancel();
        }
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setDayTime(J)V"))
    private void neotenet$timeSkip(ServerLevel world, long time) {
        TimeSkipEvent event = new TimeSkipEvent(this.getWorld(), TimeSkipEvent.SkipReason.NIGHT_SKIP, (time - time % 24000L) - this.getDayTime());
        Bukkit.getPluginManager().callEvent(event);
        neotenet$timeSkipCancelled.set(event.isCancelled());
        if (!event.isCancelled()) {
            world.setDayTime(this.getDayTime() + event.getSkipAmount());
        }
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;wakeUpAllPlayers()V"))
    private void neotenet$notWakeIfCancelled(ServerLevel world) {
        if (!neotenet$timeSkipCancelled.get()) {
            this.wakeUpAllPlayers();
        }
        neotenet$timeSkipCancelled.set(false);
    }

    @Override
    public boolean addEntitySerialized(Entity entity, CreatureSpawnEvent.SpawnReason reason) {
        return addWithUUID(entity, reason);
    }
}
