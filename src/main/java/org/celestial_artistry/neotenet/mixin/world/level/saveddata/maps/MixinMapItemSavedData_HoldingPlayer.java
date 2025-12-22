package org.celestial_artistry.neotenet.mixin.world.level.saveddata.maps;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.map.CraftMapCursor;
import org.bukkit.craftbukkit.map.RenderData;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MapItemSavedData.HoldingPlayer.class)
public abstract class MixinMapItemSavedData_HoldingPlayer {

    @Shadow
    @Final
    public Player player;
    @Shadow
    @Final
    MapItemSavedData this$0;
    @Unique
    private final byte[] neotenet$colors = this$0.colors;
    @Unique
    private final Collection<MapDecoration> icons = new java.util.ArrayList<>();

    private final AtomicReference<RenderData> neotenet$render = new AtomicReference<>();
    private final AtomicReference<Player> neotenet$player = new AtomicReference<>();

    @Inject(method = "<init>", at = @At("RETURN"))
    private void neotenet$initRender(MapItemSavedData mapItemSavedData, Player player, CallbackInfo ci) {
        neotenet$player.set(player);
    }

    @Inject(method = "nextUpdatePacket", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/saveddata/maps/MapItemSavedData$HoldingPlayer;createPatch()Lnet/minecraft/world/level/saveddata/maps/MapItemSavedData$MapPatch;"))
    private void neotenet$checkColors(MapId mapId, CallbackInfoReturnable<Packet<?>> cir) {
        RenderData render = this$0.mapView.render((CraftPlayer) this.neotenet$player.getAndSet(null).getBukkitEntity()); // CraftBukkit
        neotenet$render.set(render);
        this$0.colors = render.buffer;
    }

    @Inject(method = "nextUpdatePacket", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/saveddata/maps/MapItemSavedData$HoldingPlayer;createPatch()Lnet/minecraft/world/level/saveddata/maps/MapItemSavedData$MapPatch;",
            shift = At.Shift.AFTER))
    private void neotenet$setColors(MapId mapId, CallbackInfoReturnable<Packet<?>> cir) {
        this$0.colors = neotenet$colors;
    }

    @Redirect(method = "nextUpdatePacket", at = @At(value = "INVOKE", target = "Ljava/util/Map;values()Ljava/util/Collection;"))
    private Collection<MapDecoration> neotenet$resetCollections(Map instance) {
        // CraftBukkit start
        for (org.bukkit.map.MapCursor cursor : neotenet$render.getAndSet(null).cursors) {
            if (cursor.isVisible()) {
                icons.add(new MapDecoration(CraftMapCursor.CraftType.bukkitToMinecraftHolder(cursor.getType()), cursor.getX(), cursor.getY(), cursor.getDirection(), CraftChatMessage.fromStringOrOptional(cursor.getCaption())));
            }
        }
        return icons;
        // CraftBukkit end
    }
}