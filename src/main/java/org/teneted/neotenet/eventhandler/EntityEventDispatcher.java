package org.teneted.neotenet.eventhandler;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.monster.Bogged;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.internal.versions.neoforge.NeoForgeVersion;
import org.bukkit.event.entity.EntityTargetEvent;

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
}
