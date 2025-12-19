package org.taiyitistmc.mixin.server.players;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.PlayerDataStorage;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.command.ColouredConsoleSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.taiyitistmc.injection.server.players.InjectionPlayerList;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Mixin(PlayerList.class)
public class MixinPlayerList implements InjectionPlayerList {

    @Mutable
    @Shadow
    @Final
    public List<ServerPlayer> players;
    @Shadow
    private CraftServer cserver;

    @Inject(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/server/players/PlayerList;bans:Lnet/minecraft/server/players/UserBanList;"))
    public void taiyitist$init(MinecraftServer p_203842_, LayeredRegistryAccess p_251844_, PlayerDataStorage p_203844_, int p_203845_, CallbackInfo ci) {
        this.players = new CopyOnWriteArrayList<>();
        this.cserver = new CraftServer((DedicatedServer) p_203842_, ((PlayerList) (Object) this));
        p_203842_.console = ColouredConsoleSender.getInstance();
    }

    @Inject(method = "placeNewPlayer", at = @At(value = "INVOKE",
            target = "Ljava/util/Optional;flatMap(Ljava/util/function/Function;)Ljava/util/Optional;"), locals = LocalCapture.CAPTURE_FAILSOFT)
    public void taiyitist$print(Connection p_11262_, ServerPlayer p_11263_, CommonListenerCookie p_301988_, CallbackInfo ci, GameProfile gameprofile, GameProfileCache gameprofilecache, String s, Optional optional1) {
        // CraftBukkit start - Better rename detection
        if (optional1.isPresent()) {
            CompoundTag nbttagcompound = (CompoundTag) optional1.get();
            if (nbttagcompound.contains("bukkit")) {
                CompoundTag bukkit = nbttagcompound.getCompound("bukkit");
                s = bukkit.contains("lastKnownName", 8) ? bukkit.getString("lastKnownName") : s;
            }
        }
    }

    @Redirect(method = "placeNewPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;getLevel(Lnet/minecraft/resources/ResourceKey;)Lnet/minecraft/server/level/ServerLevel;"))
    private ServerLevel taiyitist$spawnLocationEvent(MinecraftServer minecraftServer, ResourceKey<Level> dimension, Connection netManager, ServerPlayer playerIn) {
        CraftPlayer player = playerIn.getBukkitEntity();
        PlayerSpawnLocationEvent event = new PlayerSpawnLocationEvent(player, player.getLocation());
        cserver.getPluginManager().callEvent(event);
        Location loc = event.getSpawnLocation();
        ServerLevel world = ((CraftWorld) loc.getWorld()).getHandle();
        playerIn.setServerLevel(world);
        playerIn.absMoveTo(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        return world;
    }

    @Redirect(method = "placeNewPlayer", at = @At(value = "FIELD", target = "Lnet/minecraft/server/players/PlayerList;viewDistance:I"))
    private int taiyitist$spigotViewDistance(PlayerList playerList, Connection netManager, ServerPlayer playerIn) {
        return playerIn.serverLevel().spigotConfig.viewDistance;
    }

    @Redirect(method = "placeNewPlayer", at = @At(value = "FIELD", target = "Lnet/minecraft/server/players/PlayerList;simulationDistance:I"))
    private int taiyitist$spigotSimDistance(PlayerList instance, Connection netManager, ServerPlayer playerIn) {
        return playerIn.serverLevel().spigotConfig.simulationDistance;
    }

    @Override
    public CraftServer getCraftServer() {
        return this.cserver;
    }
}
