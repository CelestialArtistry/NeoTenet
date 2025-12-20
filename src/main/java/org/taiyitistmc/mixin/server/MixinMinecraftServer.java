package org.taiyitistmc.mixin.server;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.DataFixer;
import it.unimi.dsi.fastutil.longs.LongIterator;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerInfo;
import net.minecraft.server.Services;
import net.minecraft.server.TickTask;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.WorldStem;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.network.ServerConnectionListener;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.util.Mth;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ForcedChunksSavedData;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.storage.ChunkIOErrorReporter;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.WorldData;
import net.neoforged.neoforge.common.world.chunk.ForcedChunkManager;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.Main;
import org.bukkit.craftbukkit.scoreboard.CraftScoreboardManager;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.plugin.PluginLoadOrder;
import org.objectweb.asm.Opcodes;
import org.spigotmc.WatchdogThread;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.taiyitistmc.bukkit.BukkitSnapshotCaptures;
import org.taiyitistmc.injection.server.InjectionMinecraftServer;

import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.locks.LockSupport;
import java.util.logging.Level;
import java.util.logging.Logger;

@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer extends ReentrantBlockableEventLoop<TickTask> implements ServerInfo, ChunkIOErrorReporter, CommandSource, AutoCloseable, InjectionMinecraftServer {

    @Mutable
    @Shadow
    @Final
    private static long OVERLOADED_THRESHOLD_NANOS;

    @Shadow
    public OptionSet options;

    @Shadow
    public Commands vanillaCommandDispatcher;

    @Shadow
    public WorldLoader.DataLoadContext worldLoader;

    @Shadow
    public CraftServer server;

    @Shadow
    public ServerConnectionListener connection;

    @Shadow
    protected static void setInitialSpawn(ServerLevel p_177897_, ServerLevelData p_177898_, boolean p_177899_, boolean p_177900_) {
    }

    @Shadow
    protected abstract void setupDebugLevel(WorldData p_129848_);

    @Shadow
    public WorldData worldData;

    @Shadow
    public Map<ResourceKey<net.minecraft.world.level.Level>, ServerLevel> levels;

    @Shadow
    @Deprecated
    public abstract void markWorldsDirty();

    @Shadow
    private boolean forceTicks;

    @Shadow
    protected long nextTickTimeNanos;

    @Shadow
    @Final
    public static org.slf4j.Logger LOGGER;

    @Shadow
    @Final
    private Object stopLock;

    @Shadow
    private boolean hasStopped;

    @Shadow
    public abstract boolean isSpawningAnimals();

    @Shadow
    private boolean mayHaveDelayedTasks;

    @Shadow
    private long delayedTasksMaxNextTickTimeNanos;

    public MixinMinecraftServer(String p_18765_) {
        super(p_18765_);
    }

    @Inject(method = "setInitialSpawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerChunkCache;getGenerator()Lnet/minecraft/world/level/chunk/ChunkGenerator;"), cancellable = true)
    private static void taiyitist$spawnInit(ServerLevel level, ServerLevelData levelData, boolean generateBonusChest, boolean debug, CallbackInfo ci) {
        // CraftBukkit start
        if (level.generator != null) {
            Random rand = new Random(level.getSeed());
            org.bukkit.Location spawn = level.generator.getFixedSpawnLocation(level.getWorld(), rand);

            if (spawn != null) {
                if (spawn.getWorld() != level.getWorld()) {
                    throw new IllegalStateException("Cannot set spawn point for " + levelData.getLevelName() + " to be in another world (" + spawn.getWorld().getName() + ")");
                } else {
                    levelData.setSpawn(new BlockPos(spawn.getBlockX(), spawn.getBlockY(), spawn.getBlockZ()), spawn.getYaw());
                    ci.cancel();
                }
            }
        }
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void taiyitist$loadOptions(Thread p_236723_, LevelStorageSource.LevelStorageAccess p_236724_, PackRepository p_236725_, WorldStem p_236726_, java.net.Proxy p_236727_, DataFixer p_236728_, Services p_236729_, ChunkProgressListenerFactory p_236730_, CallbackInfo ci) {
        OVERLOADED_THRESHOLD_NANOS = 30L * TimeUtil.NANOSECONDS_PER_SECOND / 20L; // CraftBukkit
        String[] arguments = ManagementFactory.getRuntimeMXBean().getInputArguments().toArray(new String[0]);
        OptionParser parser = new Main();
        try {
            options = parser.parse(arguments);
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, ex.getLocalizedMessage());
        }
        Main.handleParser(parser, options);
        this.vanillaCommandDispatcher = p_236726_.dataPackResources().getCommands();
        this.worldLoader = BukkitSnapshotCaptures.getDataLoadContext();
    }

    @Inject(method = "stopServer", at = @At(value = "INVOKE", remap = false, ordinal = 0, shift = At.Shift.AFTER, target = "Lorg/slf4j/Logger;info(Ljava/lang/String;)V"))
    public void taiyitist$unloadPlugins(CallbackInfo ci) {
        if (this.server != null) {
            this.server.disablePlugins();
        }
    }

    @Inject(method = "runServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;onServerExit()V"))
    private void taiyitist$watchdogExit(CallbackInfo ci) {
        WatchdogThread.doStop();
    }

    @Inject(method = "stopServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;removeAll()V"))
    private void taiyitist$stopThread(CallbackInfo ci) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {
        } // CraftBukkit - SPIGOT-625 - give server at least a chance to send packets
    }

    @Inject(method = "stopServer", at = @At("HEAD"), cancellable = true)
    private void taiyitist$preventMultiple(CallbackInfo ci) {
        // CraftBukkit start - prevent double stopping on multiple threads
        synchronized(stopLock) {
            if (hasStopped) return; ci.cancel();
            hasStopped = true;
        }
    }

    @Inject(method = "loadLevel", at = @At("RETURN"))
    public void taiyitist$enablePlugins(CallbackInfo ci) {
        this.server.enablePlugins(PluginLoadOrder.POSTWORLD);
        this.server.getPluginManager().callEvent(new ServerLoadEvent(ServerLoadEvent.LoadType.STARTUP));
        this.connection.acceptConnections();
    }

    @Inject(method = "runServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;startMetricsRecordingTick()V"))
    private void taiyitist$markTick(CallbackInfo ci) {
        MinecraftServer.currentTick = (int) (System.currentTimeMillis() / 50); // CraftBukkit
    }

    @Inject(method = "createLevels", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;getDataStorage()Lnet/minecraft/world/level/storage/DimensionDataStorage;"))
    private void taiyitist$worldInitEvent(ChunkProgressListener p_129816_, CallbackInfo ci, @Local ServerLevel serverLevel) {
        // CraftBukkit start
        if (serverLevel.generator != null) {
            serverLevel.getWorld().getPopulators().addAll(serverLevel.generator.getDefaultPopulators(serverLevel.getWorld()));
        }
        Bukkit.getPluginManager().callEvent(new WorldInitEvent(serverLevel.getWorld()));
    }
    // CraftBukkit start

    @Inject(method = "createLevels", at = @At(value = "FIELD", target = "Lnet/minecraft/server/MinecraftServer;commandStorage:Lnet/minecraft/world/level/storage/CommandStorage;", opcode = Opcodes.PUTFIELD))
    private void taiyitist$craftScoreboard(ChunkProgressListener p_129816_, CallbackInfo ci, @Local ServerLevel serverLevel) {
        // CraftBukkit start
        this.server.scoreboardManager = new CraftScoreboardManager(((MinecraftServer) (Object) this), serverLevel.getScoreboard());
    }
    // CraftBukkit start

    @Inject(method = "createLevels", at = @At(value = "FIELD", target = "Lnet/minecraft/server/MinecraftServer;commandStorage:Lnet/minecraft/world/level/storage/CommandStorage;", opcode = Opcodes.PUTFIELD))
    private void taiyitist$craftWorld(ChunkProgressListener p_129816_, CallbackInfo ci, @Local ServerLevel serverLevel) {
        // CraftBukkit start
        this.server.scoreboardManager = new CraftScoreboardManager(((MinecraftServer) (Object) this), serverLevel.getScoreboard());
    }
    // CraftBukkit start

    /**
     * @author wdog5734
     * @reason bukkit
     */
    @Overwrite
    private boolean haveTime() {
        // CraftBukkit start
        return this.forceTicks || this.runningTask() || Util.getNanos() < (this.mayHaveDelayedTasks ? this.delayedTasksMaxNextTickTimeNanos : this.nextTickTimeNanos);
    }

    @Override
    public void initWorld(ServerLevel serverlevel, ServerLevelData serverleveldata, WorldData saveData, WorldOptions worldoptions) {
        boolean flag = saveData.isDebugWorld();
        // CraftBukkit start
        if (serverlevel.generator != null) {
            serverlevel.getWorld().getPopulators().addAll(serverlevel.generator.getDefaultPopulators(serverlevel.getWorld()));
        }
        WorldBorder worldborder = serverlevel.getWorldBorder();
        worldborder.applySettings(serverleveldata.getWorldBorder()); // CraftBukkit - move up so that WorldBorder is set during WorldInitEvent
        Bukkit.getPluginManager().callEvent(new WorldInitEvent(serverlevel.getWorld()));

        if (!serverleveldata.isInitialized()) {
            try {
                setInitialSpawn(serverlevel, serverleveldata, worldoptions.generateBonusChest(), flag);
                serverleveldata.setInitialized(true);
                if (flag) {
                    this.setupDebugLevel(this.worldData);
                }
            } catch (Throwable throwable1) {
                CrashReport crashreport = CrashReport.forThrowable(throwable1, "Exception initializing level");

                try {
                    serverlevel.fillReportDetails(crashreport);
                } catch (Throwable throwable) {
                }

                throw new ReportedException(crashreport);
            }

            serverleveldata.setInitialized(true);
        }
    }
    // CraftBukkit end

    @Override
    public void executeModerately() {
        this.runAllTasks();
        LockSupport.parkNanos("executing tasks", 1000L);
        // CraftBukkit end
    }

    // CraftBukkit start
    @Override
    public void addLevel(ServerLevel level) {
        this.levels.put(level.dimension(), level); // Taiyitist
        markWorldsDirty();
    }

    @Override
    public void removeLevel(ServerLevel level) {
        this.levels.remove(level.dimension()); // Taiyitist
        markWorldsDirty();
        ((CraftServer)Bukkit.getServer()).removeWorld(level);
    }
    // CraftBukkit end

    @Override
    public boolean isDebugging() {
        return false;
    }

    @Override
    public void prepareLevels(ChunkProgressListener p_129941_, ServerLevel serverlevel) {
        markWorldsDirty();
        if (!serverlevel.getWorld().getKeepSpawnInMemory()) {
            return;
        }
        this.forceTicks = true;
        LOGGER.info("Preparing start region for dimension {}", serverlevel.dimension().location());
        BlockPos blockpos = serverlevel.getSharedSpawnPos();
        p_129941_.updateSpawnPos(new ChunkPos(blockpos));
        ServerChunkCache serverchunkcache = serverlevel.getChunkSource();
        this.nextTickTimeNanos = Util.getNanos();
        serverlevel.setDefaultSpawnPos(blockpos, serverlevel.getSharedSpawnAngle());
        int i = serverlevel.getGameRules().getInt(GameRules.RULE_SPAWN_CHUNK_RADIUS);
        int j = i > 0 ? Mth.square(ChunkProgressListener.calculateDiameter(i)) : 0;

        while (serverchunkcache.getTickingGenerated() < j) {
            this.executeModerately();
        }

        this.executeModerately();
        ForcedChunksSavedData forcedchunkssaveddata = serverlevel.getDataStorage().get(ForcedChunksSavedData.factory(), "chunks");
        if (forcedchunkssaveddata != null) {
            LongIterator longiterator = forcedchunkssaveddata.getChunks().iterator();

            while (longiterator.hasNext()) {
                long k = longiterator.nextLong();
                ChunkPos chunkpos = new ChunkPos(k);
                serverlevel.getChunkSource().updateChunkForced(chunkpos, true);
            }
            ForcedChunkManager.reinstatePersistentChunks(serverlevel, forcedchunkssaveddata);
        }
        Bukkit.getPluginManager().callEvent(new WorldLoadEvent(serverlevel.getWorld()));

        // CraftBukkit start
        this.executeModerately();
        p_129941_.stop();
        serverlevel.setSpawnSettings(serverlevel.K.getDifficulty() != Difficulty.PEACEFUL && ((DedicatedServer) (Object) this).settings.getProperties().spawnMonsters, this.isSpawningAnimals()); // Paper - per level difficulty (from setDifficulty(ServerLevel, Difficulty, boolean))
        this.forceTicks = false;
        // CraftBukkit end
    }

    @Override
    public final boolean hasStopped() {
        synchronized (stopLock) {
            return hasStopped;
        }
    }
}
