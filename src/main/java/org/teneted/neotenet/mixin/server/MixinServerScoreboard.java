package org.teneted.neotenet.mixin.server;

import net.minecraft.network.protocol.Packet;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.stream.Collectors;

@Mixin(ServerScoreboard.class)
public class MixinServerScoreboard {

    @Redirect(method = "startTrackingObjective", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;getPlayers()Ljava/util/List;"))
    private List<ServerPlayer> neotenet$filterAdd(PlayerList playerList) {
        return filterPlayer(playerList.getPlayers());
    }

    @Redirect(method = "stopTrackingObjective", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;getPlayers()Ljava/util/List;"))
    private List<ServerPlayer> neotenet$filterRemove(PlayerList playerList) {
        return filterPlayer(playerList.getPlayers());
    }

    @Redirect(method = "onScoreChanged", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastAll(Lnet/minecraft/network/protocol/Packet;)V"))
    private void neotenet$sendToOwner0(PlayerList playerList, Packet<?> packetIn) {
        for (ServerPlayer entity : filterPlayer(playerList.getPlayers())) {
            entity.connection.send(packetIn);
        }
    }

    @Redirect(method = "onPlayerRemoved", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastAll(Lnet/minecraft/network/protocol/Packet;)V"))
    private void neotenet$sendToOwner1(PlayerList playerList, Packet<?> packetIn) {
        for (ServerPlayer entity : filterPlayer(playerList.getPlayers())) {
            entity.connection.send(packetIn);
        }
    }

    @Redirect(method = "onPlayerScoreRemoved", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastAll(Lnet/minecraft/network/protocol/Packet;)V"))
    private void neotenet$sendToOwner2(PlayerList playerList, Packet<?> packetIn) {
        for (ServerPlayer entity : filterPlayer(playerList.getPlayers())) {
            entity.connection.send(packetIn);
        }
    }

    @Redirect(method = "setDisplayObjective", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastAll(Lnet/minecraft/network/protocol/Packet;)V"))
    private void neotenet$sendToOwner3(PlayerList playerList, Packet<?> packetIn) {
        for (ServerPlayer entity : filterPlayer(playerList.getPlayers())) {
            entity.connection.send(packetIn);
        }
    }

    @Redirect(method = "addPlayerToTeam", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastAll(Lnet/minecraft/network/protocol/Packet;)V"))
    private void neotenet$sendToOwner4(PlayerList playerList, Packet<?> packetIn) {
        for (ServerPlayer entity : filterPlayer(playerList.getPlayers())) {
            entity.connection.send(packetIn);
        }
    }

    @Redirect(method = "removePlayerFromTeam", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastAll(Lnet/minecraft/network/protocol/Packet;)V"))
    private void neotenet$sendToOwner5(PlayerList playerList, Packet<?> packetIn) {
        for (ServerPlayer entity : filterPlayer(playerList.getPlayers())) {
            entity.connection.send(packetIn);
        }
    }

    @Redirect(method = "onObjectiveChanged", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastAll(Lnet/minecraft/network/protocol/Packet;)V"))
    private void neotenet$sendToOwner6(PlayerList playerList, Packet<?> packetIn) {
        for (ServerPlayer entity : filterPlayer(playerList.getPlayers())) {
            entity.connection.send(packetIn);
        }
    }

    @Redirect(method = "onTeamAdded", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastAll(Lnet/minecraft/network/protocol/Packet;)V"))
    private void neotenet$sendToOwner7(PlayerList playerList, Packet<?> packetIn) {
        for (ServerPlayer entity : filterPlayer(playerList.getPlayers())) {
            entity.connection.send(packetIn);
        }
    }

    @Redirect(method = "onTeamChanged", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastAll(Lnet/minecraft/network/protocol/Packet;)V"))
    private void neotenet$sendToOwner8(PlayerList playerList, Packet<?> packetIn) {
        for (ServerPlayer entity : filterPlayer(playerList.getPlayers())) {
            entity.connection.send(packetIn);
        }
    }

    @Redirect(method = "onTeamRemoved", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastAll(Lnet/minecraft/network/protocol/Packet;)V"))
    private void neotenet$sendToOwner9(PlayerList playerList, Packet<?> packetIn) {
        for (ServerPlayer entity : filterPlayer(playerList.getPlayers())) {
            entity.connection.send(packetIn);
        }
    }

    private List<ServerPlayer> filterPlayer(List<ServerPlayer> list) {
        return list.stream()
            .filter(it -> ((ServerPlayer) it).getBukkitEntity().getScoreboard().getHandle() == (Object) this)
            .collect(Collectors.toList());
    }
}
