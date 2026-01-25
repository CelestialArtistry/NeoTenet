package org.teneted.neotenet.eventhandler;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.monster.Bogged;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.item.ItemExpireEvent;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.internal.versions.neoforge.NeoForgeVersion;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

@EventBusSubscriber(modid = NeoForgeVersion.MOD_ID)
public class EntityEventDispatcher {

    @SubscribeEvent
    public static void onEntityShear(PlayerInteractEvent.EntityInteract event) {
        var player = event.getEntity();
        var target = event.getTarget();
        var itemstack = event.getItemStack();
        var hand = event.getHand();
        if (itemstack.is(Items.SHEARS)) {
            if (target instanceof Bogged bogged) {
                // CraftBukkit start
                if (!org.bukkit.craftbukkit.event.CraftEventFactory.handlePlayerShearEntityEvent(player, bogged, itemstack, hand)) {
                    bogged.getEntityData().markDirty(Bogged.getDataSheared()); // CraftBukkit - mark dirty to restore sheared state to clients
                    event.setCancellationResult(InteractionResult.PASS);
                }
                // CraftBukkit end
            }
        }
    }

    @SubscribeEvent
    public static void onEntityTarget(LivingChangeTargetEvent event) {
        var entity = event.getEntity();
        if (entity instanceof Vex vex) {
            vex.bridge$pushGoalTargetReason(EntityTargetEvent.TargetReason.OWNER_ATTACKED_TARGET, true);
        }
    }

    @SubscribeEvent
    public static void onItemExpire(ItemExpireEvent event) {
        var entity = event.getEntity();
        // CraftBukkit start - fire ItemDespawnEvent
        if (CraftEventFactory.callItemDespawnEvent(entity).isCancelled()) {
            entity.age = 0;
            return;
        }
        // CraftBukkit end
    }

    @SubscribeEvent
    public static void onItemPickup(ItemEntityPickupEvent.Post event) {
        var itemEntity = event.getItemEntity();
        var player = event.getPlayer();
        var itemstack = event.getOriginalStack();
        int i = itemstack.getCount();
        // CraftBukkit start - fire PlayerPickupItemEvent
        int canHold = player.getInventory().canHold(itemstack);
        int remaining = i - canHold;

        if (itemEntity.pickupDelay <= 0 && canHold > 0) {
            itemstack.setCount(canHold);
            // Call legacy event
            PlayerPickupItemEvent playerEvent = new PlayerPickupItemEvent((org.bukkit.entity.Player) player.getBukkitEntity(), (org.bukkit.entity.Item) itemEntity.getBukkitEntity(), remaining);
            playerEvent.setCancelled(!playerEvent.getPlayer().getCanPickupItems());
            itemEntity.level().getCraftServer().getPluginManager().callEvent(playerEvent);
            if (playerEvent.isCancelled()) {
                itemstack.setCount(i); // SPIGOT-5294 - restore count
                return;
            }

            // Call newer event afterwards
            EntityPickupItemEvent entityEvent = new EntityPickupItemEvent((org.bukkit.entity.Player) player.getBukkitEntity(), (org.bukkit.entity.Item) itemEntity.getBukkitEntity(), remaining);
            entityEvent.setCancelled(!entityEvent.getEntity().getCanPickupItems());
            itemEntity.level().getCraftServer().getPluginManager().callEvent(entityEvent);
            if (entityEvent.isCancelled()) {
                itemstack.setCount(i); // SPIGOT-5294 - restore count
                return;
            }

            // Update the ItemStack if it was changed in the event
            ItemStack current = itemEntity.getItem();
            if (!itemstack.equals(current)) {
                itemstack = current;
            } else {
                itemstack.setCount(canHold + remaining); // = i
            }

            // Possibly < 0; fix here so we do not have to modify code below
            itemEntity.pickupDelay = 0;
        } else if (itemEntity.pickupDelay == 0) {
            // ensure that the code below isn't triggered if canHold says we can't pick the items up
            itemEntity.pickupDelay = -1;
        }
        // CraftBukkit end
    }

}
