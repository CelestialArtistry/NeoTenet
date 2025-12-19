package org.taiyitistmc.mixin.server.dedicated;

import com.mojang.datafixers.DataFixer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Services;
import net.minecraft.server.WorldStem;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecrell.terminalconsole.TerminalConsoleAppender;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.io.IoBuilder;
import org.bukkit.craftbukkit.util.ForwardLogHandler;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginLoadOrder;
import org.spigotmc.SpigotConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.taiyitistmc.NeoTaiyitist;

import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Logger;

@Mixin(DedicatedServer.class)
public abstract class MixinDedicatedServer extends MinecraftServer {

    public MixinDedicatedServer(Thread p_236723_, LevelStorageSource.LevelStorageAccess p_236724_, PackRepository p_236725_, WorldStem p_236726_, Proxy p_236727_, DataFixer p_236728_, Services p_236729_, ChunkProgressListenerFactory p_236730_) {
        super(p_236723_, p_236724_, p_236725_, p_236726_, p_236727_, p_236728_, p_236729_, p_236730_);
    }

    @Inject(method = "initServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/dedicated/DedicatedServer;usesAuthentication()Z", ordinal = 1))
    private void taiyitist$initServer(CallbackInfoReturnable<Boolean> cir) {
        // CraftBukkit start
        SpigotConfig.init((File) this.options.valueOf("spigot-settings"));
        SpigotConfig.registerCommands();
        this.server.loadPlugins();
        this.server.enablePlugins(PluginLoadOrder.STARTUP);
    }

    @Inject(method = "initServer",
            at = @At(value = "INVOKE",
                    target = "Ljava/lang/Thread;setDaemon(Z)V",
                    ordinal = 0,
                    shift = At.Shift.BEFORE))
    private void taiyitist$addLog4j(CallbackInfoReturnable<Boolean> cir) {
        // CraftBukkit start - TODO: handle command-line logging arguments
        Logger global = Logger.getLogger("");
        global.setUseParentHandlers(false);
        for (Handler handler : global.getHandlers()) {
            global.removeHandler(handler);
        }
        global.addHandler(new ForwardLogHandler());
        final org.apache.logging.log4j.Logger logger = LogManager.getRootLogger();

        System.setOut(IoBuilder.forLogger(logger).setLevel(Level.INFO).buildPrintStream());
        System.setErr(IoBuilder.forLogger(logger).setLevel(Level.WARN).buildPrintStream());
        // CraftBukkit end
    }

    @Inject(method = "getPluginNames", at = @At("RETURN"), cancellable = true)
    private void taiyitist$setPluginNames(CallbackInfoReturnable<String> cir) {
        StringBuilder result = new StringBuilder();
        Plugin[] plugins = server.getPluginManager().getPlugins();

        result.append(server.getName());
        result.append(" on Bukkit ");
        result.append(server.getBukkitVersion());

        if (plugins.length > 0 && server.getQueryPlugins()) {
            result.append(": ");

            for (int i = 0; i < plugins.length; i++) {
                if (i > 0) {
                    result.append("; ");
                }

                result.append(plugins[i].getDescription().getName());
                result.append(" ");
                result.append(plugins[i].getDescription().getVersion().replaceAll(";", ","));
            }
        }

        cir.setReturnValue(result.toString());
    }

    @Inject(method = "onServerExit", at = @At("RETURN"))
    public void taiyitist$exitNow(CallbackInfo ci) {
        try {
            TerminalConsoleAppender.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Thread exitThread = new Thread(this::taiyitist$exit, "Exit Thread");
        exitThread.setDaemon(true);
        exitThread.start();
    }

    private void taiyitist$exit() {
        try {
            Thread.sleep(5000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<String> threads = new ArrayList<>();
        for (Thread thread : Thread.getAllStackTraces().keySet()) {
            if (!thread.isDaemon() && !thread.getName().equals("DestroyJavaVM")) {
                threads.add(thread.getName());
            }
        }
        if (!threads.isEmpty()) {
            NeoTaiyitist.LOGGER.debug("Threads {} not shutting down", String.join(", ", threads));
            NeoTaiyitist.LOGGER.info("{} threads not shutting down correctly, force exiting", threads.size());
        }
        System.exit(0);
    }


    @Inject(at = @At("HEAD"), method = "showGui", cancellable = true)
    public void taiyitist$nogui(CallbackInfo ci) {
        ci.cancel();
    }
}
