package org.taiyitistmc.mixin.server.level;

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
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.block.CraftBlockState;
import org.bukkit.craftbukkit.block.CraftBlockStates;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.generator.CustomWorldChunkManager;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.craftbukkit.util.WorldUUID;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LightningStrike;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.event.world.GenericGameEvent;
import org.bukkit.event.world.TimeSkipEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.generator.BiomeProvider;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.taiyitistmc.bukkit.DistValidate;
import org.taiyitistmc.injection.server.level.InjectionServerLevel;

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
    @Final
    private MinecraftServer server;

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

    @Inject(method = "<init>(Lnet/minecraft/server/MinecraftServer;Ljava/util/concurrent/Executor;Lnet/minecraft/world/level/storage/LevelStorageSource$LevelStorageAccess;Lnet/minecraft/world/level/storage/ServerLevelData;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/world/level/dimension/LevelStem;Lnet/minecraft/server/level/progress/ChunkProgressListener;ZJLjava/util/List;ZLnet/minecraft/world/RandomSequences;)V", at = @At("RETURN"))
    private void taiyitist$init(MinecraftServer p_214999_, Executor p_215000_, LevelStorageSource.LevelStorageAccess p_215001_, ServerLevelData p_215002_, ResourceKey p_215003_, LevelStem p_215004_, ChunkProgressListener p_215005_, boolean p_215006_, long p_215007_, List p_215008_, boolean p_215009_, RandomSequences p_288977_, CallbackInfo ci, @Local ChunkGenerator chunkgenerator) {
        this.pvpMode = p_214999_.isPvpAllowed();
        convertable = p_215001_;
        uuid = WorldUUID.getUUID(convertable.levelDirectory.path().toFile());
        // CraftBukkit end
        // CraftBukkit start
        K.setWorld(((ServerLevel) (Object) this));

        if (biomeProvider != null) {
            BiomeSource worldChunkManager = new CustomWorldChunkManager(getWorld(), biomeProvider, server.registryAccess().registryOrThrow(Registries.BIOME));
            if (chunkgenerator instanceof NoiseBasedChunkGenerator cga) {
                chunkgenerator = new NoiseBasedChunkGenerator(worldChunkManager, cga.settings);
            } else if (chunkgenerator instanceof FlatLevelSource cpf) {
                chunkgenerator = new FlatLevelSource(cpf.settings(), worldChunkManager);
            }
        }

        if (generator != null) {
            chunkgenerator = new org.bukkit.craftbukkit.generator.CustomChunkGenerator(((ServerLevel) (Object) this), chunkgenerator, generator);
        }
        // CraftBukkit end
    }

    @Inject(method = "<init>(Lnet/minecraft/server/MinecraftServer;Ljava/util/concurrent/Executor;Lnet/minecraft/world/level/storage/LevelStorageSource$LevelStorageAccess;Lnet/minecraft/world/level/storage/ServerLevelData;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/world/level/dimension/LevelStem;Lnet/minecraft/server/level/progress/ChunkProgressListener;ZJLjava/util/List;ZLnet/minecraft/world/RandomSequences;)V", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/attachment/LevelAttachmentsSavedData;init(Lnet/minecraft/server/level/ServerLevel;)V"))
    private void taiyitist$addWorld(MinecraftServer p_214999_, Executor p_215000_, LevelStorageSource.LevelStorageAccess p_215001_, ServerLevelData p_215002_, ResourceKey p_215003_, LevelStem p_215004_, ChunkProgressListener p_215005_, boolean p_215006_, long p_215007_, List p_215008_, boolean p_215009_, RandomSequences p_288977_, CallbackInfo ci) {
        this.getCraftServer().addWorld(this.getWorld()); // CraftBukkit
    }

    @Inject(method = "gameEvent", cancellable = true, at = @At("HEAD"))
    private void taiyitist$gameEventEvent(Holder<GameEvent> holder, Vec3 p_215042_, GameEvent.Context context, CallbackInfo ci) {
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
    private void taiyitist$tickPortal(Entity entityIn, CallbackInfo ci) {
        entityIn.postTick();
    }

    @Inject(method = "tickPassenger", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/world/entity/Entity;rideTick()V"))
    private void taiyitist$tickPortalPassenger(Entity ridingEntity, Entity passengerEntity, CallbackInfo ci) {
        passengerEntity.postTick();
    }

    @Inject(method = "tickChunk", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    public void taiyitist$thunder(LevelChunk chunkIn, int randomTickSpeed, CallbackInfo ci) {
        pushAddEntityReason(CreatureSpawnEvent.SpawnReason.LIGHTNING);
    }

    @Redirect(method = "tickChunk", at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean taiyitist$thunder(ServerLevel serverWorld, Entity entityIn) {
        return strikeLightning(entityIn, LightningStrikeEvent.Cause.WEATHER);
    }

    @Override
    public boolean strikeLightning(Entity entity) {
        return this.strikeLightning(entity, LightningStrikeEvent.Cause.UNKNOWN);
    }

    private transient LightningStrikeEvent.Cause taiyitist$cause;
    private final AtomicReference<CreatureSpawnEvent.SpawnReason> taiyitist$reason = new AtomicReference<>();
    private final AtomicReference<Boolean> taiyitist$timeSkipCancelled = new AtomicReference<>(false);
    private transient boolean taiyitist$force;
    public AtomicBoolean canaddFreshEntity = new AtomicBoolean(false);

    @Override
    public boolean strikeLightning(Entity entity, LightningStrikeEvent.Cause cause) {
        if (taiyitist$cause != null) {
            cause = taiyitist$cause;
            taiyitist$cause = null;
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
    public void taiyitist$snowForm0(BlockPos blockPos, CallbackInfo ci, @Local(ordinal = 1) BlockPos blockPos2) {

        CraftBlockState craftBlockState = CraftBlockStates.getBlockState((ServerLevel) (Object) this, blockPos2, 3);
        craftBlockState.setData(Blocks.ICE.defaultBlockState());

        BlockFormEvent event = new BlockFormEvent(craftBlockState.getBlock(), craftBlockState);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "tickPrecipitation", cancellable = true, at = @At(value = "INVOKE", ordinal = 1, shift = At.Shift.BEFORE, target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    public void taiyitist$snowForm1(BlockPos blockPos, CallbackInfo ci, @Local(ordinal = 1) BlockState blockState2) {

        CraftBlockState craftBlockState = CraftBlockStates.getBlockState((ServerLevel) (Object) this, blockPos, 3);
        craftBlockState.setData(blockState2);

        BlockFormEvent event = new BlockFormEvent(craftBlockState.getBlock(), craftBlockState);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "tickPrecipitation", cancellable = true, at = @At(value = "INVOKE", ordinal = 2, shift = At.Shift.BEFORE, target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    public void taiyitist$snowForm2(BlockPos blockPos, CallbackInfo ci) {

        CraftBlockState craftBlockState = CraftBlockStates.getBlockState((ServerLevel) (Object) this, blockPos, 3);
        craftBlockState.setData(Blocks.SNOW.defaultBlockState());

        BlockFormEvent event = new BlockFormEvent(craftBlockState.getBlock(), craftBlockState);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "save", at = @At(value = "JUMP", ordinal = 0, opcode = Opcodes.IFNULL))
    private void taiyitist$worldSaveEvent(ProgressListener progress, boolean flush, boolean skipSave, CallbackInfo ci) {
        Bukkit.getPluginManager().callEvent(new WorldSaveEvent(getWorld()));
    }

    @Inject(method = "save", at = @At("TAIL"))
    private void taiyitist$saveAllChunks(ProgressListener progress, boolean flush, boolean skipSave, CallbackInfo ci) {
        // CraftBukkit start - moved from MinecraftServer.saveAllChunks
        if (this.serverLevelData instanceof PrimaryLevelData worldInfo) {
            worldInfo.setWorldBorder(this.getWorldBorder().createSettings());
            worldInfo.setCustomBossEvents(this.getServer().getCustomBossEvents().save(this.registryAccess()));
            this.convertable.saveDataTag(this.getServer().registryAccess(), worldInfo, this.getServer().getPlayerList().getSingleplayerData());
        }
        // CraftBukkit end
    }

    @Inject(method = "unload", at = @At("HEAD"))
    public void taiyitist$closeOnChunkUnloading(LevelChunk chunkIn, CallbackInfo ci) {
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
    public boolean taiyitist$particleVisible(ServerLevel serverWorld, ServerPlayer player, boolean longDistance, double posX, double posY, double posZ, Packet<?> packet) {
        return this.sendParticles(player, taiyitist$force, posX, posY, posZ, packet);
    }

    @Override
    public <T extends ParticleOptions> int sendParticles(T type, double posX, double posY, double posZ, int particleCount, double xOffset, double yOffset, double zOffset, double speed, boolean force) {
        taiyitist$force = force;
        return this.sendParticles(type, posX, posY, posZ, particleCount, xOffset, yOffset, zOffset, speed);
    }

    @Inject(method = "addEntity", cancellable = true, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/entity/PersistentEntitySectionManager;addNewEntity(Lnet/minecraft/world/level/entity/EntityAccess;)Z"))
    private void taiyitist$addEntityEvent(Entity entityIn, CallbackInfoReturnable<Boolean> cir) {
        CreatureSpawnEvent.SpawnReason reason = taiyitist$reason.get() == null ? CreatureSpawnEvent.SpawnReason.DEFAULT : taiyitist$reason.get();
        taiyitist$reason.set(null);
        if (DistValidate.isValid(this) && !CraftEventFactory.doEntityAddEventCalling((ServerLevel) (Object) this, entityIn, reason)) {
            cir.setReturnValue(false);
        }
    }

    @Override
    public boolean canAddFreshEntity() {
        return canaddFreshEntity.getAndSet(false);
    }

    @Redirect(method = "addFreshEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean taiyitist$fixAddFreshEntity(ServerLevel instance, Entity entity) {
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
    public void taiyitist$resetReason(Entity entityIn, CallbackInfoReturnable<Boolean> cir) {
        taiyitist$reason.set(null);
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
    private void taiyitist$returnIfPopulate(BlockPos pos, Block block, CallbackInfo ci) {
        if (populating) {
            ci.cancel();
        }
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setDayTime(J)V"))
    private void taiyitist$timeSkip(ServerLevel world, long time) {
        TimeSkipEvent event = new TimeSkipEvent(this.getWorld(), TimeSkipEvent.SkipReason.NIGHT_SKIP, (time - time % 24000L) - this.getDayTime());
        Bukkit.getPluginManager().callEvent(event);
        taiyitist$timeSkipCancelled.set(event.isCancelled());
        if (!event.isCancelled()) {
            world.setDayTime(this.getDayTime() + event.getSkipAmount());
        }
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;wakeUpAllPlayers()V"))
    private void taiyitist$notWakeIfCancelled(ServerLevel world) {
        if (!taiyitist$timeSkipCancelled.get()) {
            this.wakeUpAllPlayers();
        }
        taiyitist$timeSkipCancelled.set(false);
    }

    @Override
    public boolean addEntitySerialized(Entity entity, CreatureSpawnEvent.SpawnReason reason) {
        return addWithUUID(entity, reason);
    }
}
