package org.taiyitistmc.mixin.server.players;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.network.protocol.game.ClientboundSetChunkCacheRadiusPacket;
import net.minecraft.network.protocol.game.ClientboundSetDefaultSpawnPositionPacket;
import net.minecraft.network.protocol.game.ClientboundSetExperiencePacket;
import net.minecraft.network.protocol.game.ClientboundSetSimulationDistancePacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.server.players.IpBanList;
import net.minecraft.server.players.IpBanListEntry;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.players.UserBanList;
import net.minecraft.server.players.UserBanListEntry;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.PlayerDataStorage;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.command.ColouredConsoleSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.spigotmc.SpigotConfig;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.taiyitistmc.injection.server.players.InjectionPlayerList;
import org.taiyitistmc.neoforge.BukkitRegistry;

import javax.annotation.Nullable;
import java.io.File;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Mixin(PlayerList.class)
public abstract class MixinPlayerList implements InjectionPlayerList {

    @Mutable
    @Shadow
    @Final
    public List<ServerPlayer> players;
    @Shadow
    private CraftServer cserver;

    @Shadow
    @Final
    private MinecraftServer server;

    @Shadow
    @Nullable
    public abstract ServerPlayer getPlayer(UUID p_11260_);

    @Shadow
    public abstract void broadcastSystemMessage(Component p_240618_, boolean p_240644_);

    @Shadow
    public abstract void sendActivePlayerEffects(ServerPlayer p_348494_);

    @Shadow
    public abstract void sendLevelInfo(ServerPlayer p_11230_, ServerLevel p_11231_);

    @Shadow
    public abstract void sendPlayerPermissionLevel(ServerPlayer p_11290_);

    @Shadow
    public abstract void sendAllPlayerInfo(ServerPlayer p_11293_);

    @Shadow
    protected abstract void save(ServerPlayer p_11277_);

    @Shadow
    @Final
    private Map<UUID, ServerPlayer> playersByUUID;

    @Shadow
    public abstract UserBanList getBans();

    @Shadow
    @Final
    private UserBanList bans;

    @Shadow
    public abstract boolean isWhiteListed(GameProfile p_11294_);

    @Shadow
    public abstract IpBanList getIpBans();

    @Shadow
    @Final
    private IpBanList ipBans;

    @Shadow
    public int maxPlayers;

    @Shadow
    public abstract boolean canBypassPlayerLimit(GameProfile p_11298_);

    @Shadow
    @Final
    private static SimpleDateFormat BAN_DATE_FORMAT;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void taiyitist$init(MinecraftServer p_203842_, LayeredRegistryAccess p_251844_, PlayerDataStorage p_203844_, int p_203845_, CallbackInfo ci) {
        this.players = new CopyOnWriteArrayList<>();
        this.cserver = p_203842_.server = new CraftServer((DedicatedServer) p_203842_, ((PlayerList) (Object) this));
        BukkitRegistry.registerAll((DedicatedServer) p_203842_);
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

    @Redirect(method = "placeNewPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addNewPlayer(Lnet/minecraft/server/level/ServerPlayer;)V"))
    private void taiyitist$addNewPlayer(ServerLevel instance, ServerPlayer player) {
        if (player.level() == instance && !instance.players().contains(player)) {
            instance.addNewPlayer(player);
        }
    }

    @ModifyVariable(method = "placeNewPlayer", ordinal = 1, at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/server/level/ServerLevel;addNewPlayer(Lnet/minecraft/server/level/ServerPlayer;)V"))
    private ServerLevel taiyitist$handleWorldChanges(ServerLevel value, Connection connection, ServerPlayer player) {
        return player.serverLevel();
    }

    @Inject(method = "save", cancellable = true, at = @At("HEAD"))
    private void taiyitist$returnIfNotPersist(ServerPlayer playerIn, CallbackInfo ci) {
        if (!playerIn.persist) {
            ci.cancel();
        }
    }


    @Override
    public ServerPlayer respawn(ServerPlayer entityplayer, boolean flag, Entity.RemovalReason entity_removalreason, PlayerRespawnEvent.RespawnReason reason) {
        return this.respawn(entityplayer, flag, entity_removalreason, reason, null);
    }

    @Override
    public ServerPlayer respawn(ServerPlayer entityplayer, boolean flag, Entity.RemovalReason entity_removalreason, PlayerRespawnEvent.RespawnReason reason, Location location) {
        entityplayer.stopRiding(); // CraftBukkit
        this.players.remove(entityplayer);
        entityplayer.serverLevel().removePlayerImmediately(entityplayer, entity_removalreason);
        /* CraftBukkit start
        DimensionTransition dimensiontransition = entityplayer.findRespawnPositionAndUseSpawnBlock(flag, DimensionTransition.DO_NOTHING);
        WorldServer worldserver = dimensiontransition.newLevel();
        EntityPlayer entityplayer1 = new EntityPlayer(this.server, worldserver, entityplayer.getGameProfile(), entityplayer.clientInformation());
        // */
        ServerPlayer entityplayer1 = entityplayer;
        Level fromWorld = entityplayer.level();
        entityplayer.wonGame = false;
        // CraftBukkit end

        entityplayer1.connection = entityplayer.connection;
        entityplayer1.restoreFrom(entityplayer, flag);
        entityplayer1.setId(entityplayer.getId());
        entityplayer1.setMainArm(entityplayer.getMainArm());
        // CraftBukkit - not required, just copies old location into reused entity
        /*
        if (!dimensiontransition.missingRespawnBlock()) {
            entityplayer1.copyRespawnPosition(entityplayer);
        }
         */
        // CraftBukkit end

        Iterator iterator = entityplayer.getTags().iterator();

        while (iterator.hasNext()) {
            String s = (String) iterator.next();

            entityplayer1.addTag(s);
        }
        boolean isBedSpawn = false;
        boolean isRespawn = false;

        // CraftBukkit start - fire PlayerRespawnEvent
        DimensionTransition dimensiontransition;
        if (location == null) {
            dimensiontransition = entityplayer.findRespawnPositionAndUseSpawnBlock(flag, DimensionTransition.DO_NOTHING/*, reaso*/);

            if (!flag) entityplayer.reset(); // SPIGOT-4785
            // Paper start - Add PlayerPostRespawnEvent
            if (dimensiontransition == null) return entityplayer; // Early exit, mirrors belows early return for disconnected players in respawn event
            isRespawn = true;
            location = CraftLocation.toBukkit(dimensiontransition.pos(), dimensiontransition.newLevel().getWorld(), dimensiontransition.yRot(), dimensiontransition.xRot());
        } else {
            dimensiontransition = new DimensionTransition(((CraftWorld) location.getWorld()).getHandle(), CraftLocation.toVec3D(location), Vec3.ZERO, location.getYaw(), location.getPitch(), DimensionTransition.DO_NOTHING);
        }
        // Spigot Start
        if (dimensiontransition == null) { // Paper - Add PlayerPostRespawnEvent - diff on change - spigot early returns if respawn pos is null, that is how they handle disconnected player in respawn event
            return entityplayer;
        }
        // Spigot End
        ServerLevel worldserver = dimensiontransition.newLevel();
        entityplayer1.spawnIn(worldserver);
        entityplayer1.unsetRemoved();
        entityplayer1.setShiftKeyDown(false);
        Vec3 vec3d = dimensiontransition.pos();

        entityplayer1.forceSetPositionRotation(vec3d.x, vec3d.y, vec3d.z, dimensiontransition.yRot(), dimensiontransition.xRot());
        worldserver.getChunkSource().addRegionTicket(net.minecraft.server.level.TicketType.POST_TELEPORT, new net.minecraft.world.level.ChunkPos(net.minecraft.util.Mth.floor(vec3d.x()) >> 4, net.minecraft.util.Mth.floor(vec3d.z()) >> 4), 1, entityplayer.getId()); // Paper
        // CraftBukkit end
        if (dimensiontransition.missingRespawnBlock()) {
            entityplayer1.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.NO_RESPAWN_BLOCK_AVAILABLE, 0.0F));
            entityplayer1.setRespawnPosition(null, null, 0f, false, false); // CraftBukkit - SPIGOT-5988: Clear respawn location when obstructed // Paper - PlayerSetSpawnEvent
        }

        int i = flag ? 1 : 0;
        ServerLevel worldserver1 = entityplayer1.serverLevel();
        LevelData worlddata = worldserver1.getLevelData();

        entityplayer1.connection.send(new ClientboundRespawnPacket(entityplayer1.createCommonSpawnInfo(worldserver1), (byte) i));
        entityplayer1.connection.send(new ClientboundSetChunkCacheRadiusPacket(worldserver1.spigotConfig.viewDistance)); // Spigot
        entityplayer1.connection.send(new ClientboundSetSimulationDistancePacket(worldserver1.spigotConfig.simulationDistance)); // Spigot
        entityplayer1.connection.teleport(CraftLocation.toBukkit(entityplayer1.position(), worldserver1.getWorld(), entityplayer1.getYRot(), entityplayer1.getXRot())); // CraftBukkit
        entityplayer1.connection.send(new ClientboundSetDefaultSpawnPositionPacket(worldserver.getSharedSpawnPos(), worldserver.getSharedSpawnAngle()));
        entityplayer1.connection.send(new ClientboundChangeDifficultyPacket(worlddata.getDifficulty(), worlddata.isDifficultyLocked()));
        entityplayer1.connection.send(new ClientboundSetExperiencePacket(entityplayer1.experienceProgress, entityplayer1.totalExperience, entityplayer1.experienceLevel));
        this.sendActivePlayerEffects(entityplayer1);
        this.sendLevelInfo(entityplayer1, worldserver);
        this.sendPlayerPermissionLevel(entityplayer1);
        if (!entityplayer.connection.isDisconnected()) {
            worldserver.addRespawnedPlayer(entityplayer1);
            this.players.add(entityplayer1);
            this.playersByUUID.put(entityplayer1.getUUID(), entityplayer1);
        }
        // entityplayer1.initInventoryMenu();
        entityplayer1.setHealth(entityplayer1.getHealth());
        if (!flag) {
            BlockPos blockposition = BlockPos.containing(dimensiontransition.pos());
            BlockState iblockdata = worldserver.getBlockState(blockposition);

            if (iblockdata.is(Blocks.RESPAWN_ANCHOR)) {
                entityplayer1.connection.send(new ClientboundSoundPacket(SoundEvents.RESPAWN_ANCHOR_DEPLETE, SoundSource.BLOCKS, (double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ(), 1.0F, 1.0F, worldserver.getRandom().nextLong()));
            }
            if (iblockdata.is(net.minecraft.tags.BlockTags.BEDS) && !dimensiontransition.missingRespawnBlock()) {
                isBedSpawn = true;
            }
        }
        // Added from changeDimension
        this.sendAllPlayerInfo(entityplayer); // Update health, etc...
        entityplayer.onUpdateAbilities();
        for (MobEffectInstance mobEffect : entityplayer.getActiveEffects()) {
            entityplayer.connection.send(new ClientboundUpdateMobEffectPacket(entityplayer.getId(), mobEffect, false)); // blend = false
        }

        // Fire advancement trigger
        entityplayer.triggerDimensionChangeTriggers(worldserver);

        // Don't fire on respawn
        if (fromWorld != worldserver) {
            PlayerChangedWorldEvent event = new PlayerChangedWorldEvent(entityplayer.getBukkitEntity(), fromWorld.getWorld());
            this.server.server.getPluginManager().callEvent(event);
        }

        // Save player file again if they were disconnected
        if (entityplayer.connection.isDisconnected()) {
            this.save(entityplayer);
        }
        // CraftBukkit end

        return entityplayer1;
    }

    @Inject(method = "placeNewPlayer", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V",
            ordinal = 1))
    private void taiyitist$sendSupported(Connection connection, ServerPlayer serverPlayer, CommonListenerCookie commonListenerCookie, CallbackInfo ci) {
        serverPlayer.getBukkitEntity().sendSupportedChannels();
    }

    @Inject(method = "sendPlayerPermissionLevel(Lnet/minecraft/server/level/ServerPlayer;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;getCommands()Lnet/minecraft/commands/Commands;"))
    private void taiyitist$calculatePerms(ServerPlayer player, int permLevel, CallbackInfo ci) {
        player.getBukkitEntity().recalculatePermissions();
    }

    @Redirect(method = "sendAllPlayerInfo", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;resetSentInfo()V"))
    private void taiyitist$useScaledHealth(ServerPlayer playerEntity) {
        playerEntity.getBukkitEntity().updateScaledHealth(); // CraftBukkit - Update scaled health on respawn and worldchange
        playerEntity.refreshEntityData(playerEntity);// CraftBukkkit - SPIGOT-7218: sync metadata
        int i = playerEntity.level().getGameRules().getBoolean(GameRules.RULE_REDUCEDDEBUGINFO) ? 22 : 23;
        playerEntity.connection.send(new ClientboundEntityEventPacket(playerEntity, (byte) i));
        float immediateRespawn = playerEntity.level().getGameRules().getBoolean(GameRules.RULE_DO_IMMEDIATE_RESPAWN) ? 1.0f : 0.0f;
        playerEntity.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.IMMEDIATE_RESPAWN, immediateRespawn));
    }

    @Override
    public CraftServer getCraftServer() {
        return this.cserver;
    }

    @Override
    public void broadcastAll(Packet<?> packet, net.minecraft.world.entity.player.Player entityhuman) {
        for (ServerPlayer entityplayer : this.players) {
            if (!(entityhuman instanceof ServerPlayer) || entityplayer.getBukkitEntity().canSee(((ServerPlayer) entityhuman).getBukkitEntity())) {
                entityplayer.connection.send(packet);
            }
        }
    }

    @Override
    public void broadcastAll(Packet<?> packet, Level world) {
        for (int i = 0; i < world.players().size(); ++i) {
            ((ServerPlayer) world.players().get(i)).connection.send(packet);
        }
    }

    @Override
    public void broadcastMessage(Component[] components) {
        for (Component component : components) {
            broadcastSystemMessage(component, false);
        }
    }

    @Override
    public ServerPlayer taiyitist$canPlayerLogin(SocketAddress socketAddress, GameProfile gameProfile, ServerLoginPacketListenerImpl handler) {
        UUID uuid = gameProfile.getId();
        List<ServerPlayer> list = Lists.newArrayList();
        for (ServerPlayer player : this.players) {
            if (player.getUUID().equals(uuid)) {
                list.add(player);
            }
        }
        for (ServerPlayer player : list) {
            this.save(player);
            player.connection.disconnect(Component.translatable("multiplayer.disconnect.duplicate_login"));
        }
        ServerPlayer entity = new ServerPlayer(this.server, this.server.getLevel(Level.OVERWORLD), gameProfile, ClientInformation.createDefault());
        ((ServerPlayer) entity).transferCookieConnection = (CraftPlayer.TransferCookieConnection) handler;
        Player player = ((ServerPlayer) entity).getBukkitEntity();

        String hostname = handler == null ? "" : handler.connection.hostname;
        InetAddress realAddress = handler == null ? ((InetSocketAddress) socketAddress).getAddress() : ((InetSocketAddress) handler.connection.channel.remoteAddress()).getAddress();

        PlayerLoginEvent event = new PlayerLoginEvent(player, hostname, ((InetSocketAddress) socketAddress).getAddress(), realAddress);
        if (this.getBans().isBanned(gameProfile) && this.getBans().get(gameProfile) != null && !this.getBans().get(gameProfile).hasExpired()) {
            UserBanListEntry entry = this.bans.get(gameProfile);
            var message = Component.translatable("multiplayer.disconnect.banned.reason", entry.getReason());
            if (entry.getExpires() != null) {
                message.append(Component.translatable("multiplayer.disconnect.banned.expiration", BAN_DATE_FORMAT.format(entry.getExpires())));
            }
            event.disallow(PlayerLoginEvent.Result.KICK_BANNED, CraftChatMessage.fromComponent(message));
        } else if (!this.isWhiteListed(gameProfile)) {
            event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, SpigotConfig.whitelistMessage);
        } else if (this.getIpBans().isBanned(socketAddress) && this.getIpBans().get(socketAddress) != null && !this.getIpBans().get(socketAddress).hasExpired()) {
            IpBanListEntry entry = this.ipBans.get(socketAddress);
            var message = Component.translatable("multiplayer.disconnect.banned_ip.reason", entry.getReason());
            if (entry.getExpires() != null) {
                message.append(Component.translatable("multiplayer.disconnect.banned_ip.expiration", BAN_DATE_FORMAT.format(entry.getExpires())));
            }
            event.disallow(PlayerLoginEvent.Result.KICK_BANNED, CraftChatMessage.fromComponent(message));
        } else if (this.players.size() >= this.maxPlayers && !this.canBypassPlayerLimit(gameProfile)) {
            event.disallow(PlayerLoginEvent.Result.KICK_FULL, SpigotConfig.serverFullMessage);
        }
        this.cserver.getPluginManager().callEvent(event);
        if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
            if (handler != null) {
                handler.disconnect(CraftChatMessage.fromStringOrNull(event.getKickMessage()));
            }
            return null;
        }
        return entity;
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/players/PlayerList;broadcastAll(Lnet/minecraft/network/protocol/Packet;)V"))
    private void taiyitist$castMsg(PlayerList instance, Packet<?> packet) {
        // CraftBukkit start
        for (int i = 0; i < this.players.size(); ++i) {
            final ServerPlayer target = this.players.get(i);

            target.connection.send(new ClientboundPlayerInfoUpdatePacket(EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LATENCY), this.players.stream().filter(new Predicate<>() {
                @Override
                public boolean test(ServerPlayer input) {
                    return target.getBukkitEntity().canSee(input.getBukkitEntity());
                }
            }).collect(Collectors.toList())));
            // CraftBukkit end
        }
    }

    @Override
    public ServerStatsCounter getPlayerStats(ServerPlayer entityhuman) {
        ServerStatsCounter serverstatisticmanager = entityhuman.getStats();
        return serverstatisticmanager == null ? this.getPlayerStats(entityhuman.getUUID(), entityhuman.getName().getString()) : serverstatisticmanager;
    }

    @Override
    public ServerStatsCounter getPlayerStats(UUID uuid, String displayName) {
        ServerStatsCounter serverstatisticmanager;
        ServerPlayer entityhuman = this.getPlayer(uuid);
        ServerStatsCounter serverStatisticsManager = serverstatisticmanager = entityhuman == null ? null : entityhuman.getStats();
        if (serverstatisticmanager == null) {
            File file2;
            File file = this.server.getWorldPath(LevelResource.PLAYER_STATS_DIR).toFile();
            File file1 = new File(file, uuid + ".json");
            if (!file1.exists() && (file2 = new File(file, displayName + ".json")).exists() && file2.isFile()) {
                file2.renameTo(file1);
            }
            serverstatisticmanager = new ServerStatsCounter(this.server, file1);
        }
        return serverstatisticmanager;
    }

    @Inject(method = "removeAll", at = @At("HEAD"), cancellable = true)
    private void taiyitist$removeSafety(CallbackInfo ci) {
        for (ServerPlayer player : this.players) {
            player.connection.disconnect(CraftChatMessage.fromStringOrEmpty(this.server.server.getShutdownMessage())); // CraftBukkit - add custom shutdown message
        }
        ci.cancel();
    }

    @Redirect(method = "sendLevelInfo",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V",
                    ordinal = 3))
    private void taiyitist$cancelSendPacket0(ServerGamePacketListenerImpl instance, Packet<?> packet) {
    }

    @Redirect(method = "sendLevelInfo",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V",
                    ordinal = 4))
    private void taiyitist$cancelSendPacket1(ServerGamePacketListenerImpl instance, Packet<?> packet) {
    }

    @Redirect(method = "sendLevelInfo",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V",
                    ordinal = 5))
    private void taiyitist$cancelSendPacket2(ServerGamePacketListenerImpl instance, Packet packet) {
    }

    @Inject(method = "sendLevelInfo",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V",
                    ordinal = 3))
    private void taiyitist$setWeatherType(ServerPlayer player, ServerLevel level, CallbackInfo ci) {
        // CraftBukkit start - handle player weather
        player.setPlayerWeather(org.bukkit.WeatherType.DOWNFALL, false);
        player.updateWeather(-level.rainLevel, level.rainLevel, -level.thunderLevel, level.thunderLevel);
    }

    private final AtomicReference<ServerPlayer> taiyitist$worldBorderPlayer = new AtomicReference<>();

    @Inject(method = "sendLevelInfo", at = @At("HEAD"))
    private void taiyitist$getWorldBorderPlayer(ServerPlayer player, ServerLevel level, CallbackInfo ci) {
        taiyitist$worldBorderPlayer.set(player);
    }

    @Redirect(method = "sendLevelInfo", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerLevel;getWorldBorder()Lnet/minecraft/world/level/border/WorldBorder;"))
    private WorldBorder taiyitist$useBukkitWorldBorder(ServerLevel instance) {
        return taiyitist$worldBorderPlayer.get().level().getWorldBorder();
    }

}
