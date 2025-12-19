package org.taiyitistmc.mixin.server;

import com.mojang.datafixers.DataFixer;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Services;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.WorldStem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.network.ServerConnectionListener;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.util.TimeUtil;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.ServerLevelData;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.Main;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.PluginLoadOrder;
import org.spigotmc.WatchdogThread;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.taiyitistmc.bukkit.BukkitSnapshotCaptures;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Proxy;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {

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

    @Inject(method = "loadLevel", at = @At("RETURN"))
    public void taiyitist$enablePlugins(CallbackInfo ci) {
        this.server.enablePlugins(PluginLoadOrder.POSTWORLD);
        this.server.getPluginManager().callEvent(new ServerLoadEvent(ServerLoadEvent.LoadType.STARTUP));
        this.connection.acceptConnections();
    }
}
