package org.celestial_artistry.neotenet.mixin.server.level;

import com.google.common.collect.Lists;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.bukkit.inventory.InventoryHolder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net/minecraft/server/level/ServerLevel$EntityCallbacks")
public class MixinServerLevel_EntityCallbacks {

    @Shadow
    @Final
    ServerLevel this$0;

    @Inject(method = "onTrackingStart(Lnet/minecraft/world/entity/Entity;)V", at = @At("RETURN"))
    private void neotenet$valid(Entity entity, CallbackInfo ci) {
        entity.valid = true;
        // Paper start - Set origin location when the entity is being added to the world
        if (entity.getOriginVector() == null) {
            entity.setOrigin(entity.getBukkitEntity().getLocation());
        }
        // Default to current world if unknown, gross assumption but entities rarely change world
        if (entity.getOriginWorld() == null) {
            entity.setOrigin(entity.getOriginVector().toLocation(this$0.getWorld()));
        }
        // Paper end
    }

    @Inject(method = "onTrackingEnd(Lnet/minecraft/world/entity/Entity;)V", at = @At("TAIL"))
    private void neotenet$entityCleanup(Entity entity, CallbackInfo ci) {
        if (entity instanceof Player player) {
            for (ServerLevel serverLevel : this$0.getServer().getAllLevels()) {
                DimensionDataStorage worldData = serverLevel.getDataStorage();
                for (Object o : worldData.cache.values()) {
                    if (o instanceof MapItemSavedData map) {
                        map.carriedByPlayers.remove(player);
                        map.carriedBy.removeIf(holdingPlayer -> holdingPlayer.player == entity);
                    }
                }
            }
        }
        if (entity.getBukkitEntity() instanceof InventoryHolder holder) {
            for (org.bukkit.entity.HumanEntity h : Lists.newArrayList(holder.getInventory().getViewers())) {
                h.closeInventory();
            }
        }
    }

    @Inject(method = "onTrackingEnd(Lnet/minecraft/world/entity/Entity;)V", at = @At("RETURN"))
    private void neotenet$invalid(Entity entity, CallbackInfo ci) {
        entity.inWorld = true;
        entity.valid = false;
        if (!(entity instanceof ServerPlayer)) {
            for (var player : this$0.players()) {
                player.getBukkitEntity().onEntityRemove(entity);
            }
        }
    }
}
