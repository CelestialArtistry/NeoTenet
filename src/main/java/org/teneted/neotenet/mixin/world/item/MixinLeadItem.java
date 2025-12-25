package org.teneted.neotenet.mixin.world.item;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.LeadItem;
import net.minecraft.world.level.Level;
import org.bukkit.craftbukkit.CraftEquipmentSlot;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(LeadItem.class)
@SuppressWarnings("deprecation")
public abstract class MixinLeadItem {

    @Unique
    private static final AtomicReference<InteractionHand> neotenet$hand = new AtomicReference<>(InteractionHand.MAIN_HAND);

    @Shadow
    public static InteractionResult bindPlayerMobs(Player player, Level level, BlockPos pos) {
        return null;
    }

    @Inject(method = "bindPlayerMobs",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/decoration/LeashFenceKnotEntity;playPlacementSound()V"),
            cancellable = true)
    private static void neotenet$bindPlayerMobs(Player player, Level level, BlockPos blockPos, CallbackInfoReturnable<InteractionResult> cir, @Local(ordinal = 0) LeashFenceKnotEntity leashFenceKnotEntity) {
        // CraftBukkit start - fire HangingPlaceEvent
        org.bukkit.inventory.EquipmentSlot hand = CraftEquipmentSlot.getHand(neotenet$hand.get());
        HangingPlaceEvent event = new HangingPlaceEvent((org.bukkit.entity.Hanging) leashFenceKnotEntity.getBukkitEntity(), player != null ? (org.bukkit.entity.Player) player.getBukkitEntity() : null, level.getWorld().getBlockAt(blockPos.getX(), blockPos.getY(), blockPos.getZ()), org.bukkit.block.BlockFace.SELF, hand);
        level.getCraftServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            leashFenceKnotEntity.discard();
            cir.setReturnValue(InteractionResult.PASS);
        }
        // CraftBukkit end
    }

    @Inject(method = "bindPlayerMobs", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Leashable;setLeashedTo(Lnet/minecraft/world/entity/Entity;Z)V"))
    private static void neotenet$continueSet(Player player, Level level, BlockPos blockPos, CallbackInfoReturnable<InteractionResult> cir, @Local LeashFenceKnotEntity leashFenceKnotEntity, @Local(ordinal = 0) Leashable leashable) {
        // CraftBukkit start
        if (player != null && leashable instanceof Entity leashed && CraftEventFactory.callPlayerLeashEntityEvent(leashFenceKnotEntity, leashed, player, neotenet$hand.get()).isCancelled()) {
            cir.cancel();
        }
    }

    private static InteractionResult bindPlayerMobs(Player entityhuman, Level world, BlockPos blockposition, InteractionHand enumhand) { // CraftBukkit - Add EnumHand
        neotenet$hand.set(enumhand);
        return bindPlayerMobs(entityhuman, world, blockposition);
    }
}