package org.teneted.neotenet.mixin.world.entity.ai.behavior;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.InteractWithDoor;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryAccessor;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InteractWithDoor.class)
public class MixinInteractWithDoor {

    @Inject(method = "lambda$create$3", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/DoorBlock;setOpen(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Z)V", ordinal = 0), cancellable = true)
    private static void neotenet$interactEvent(BehaviorBuilder.Instance p_258474_, MemoryAccessor p_258460_, MemoryAccessor p_258461_, MutableObject mutableobject, MutableInt mutableint, MemoryAccessor p_258462_, ServerLevel p_258469_, LivingEntity p_258470_, long p_258471_, CallbackInfoReturnable<Boolean> cir, @Local(ordinal = 0) BlockPos blockpos) {
        // CraftBukkit start - entities opening doors
        org.bukkit.event.entity.EntityInteractEvent event = new org.bukkit.event.entity.EntityInteractEvent(p_258470_.getBukkitEntity(), org.bukkit.craftbukkit.block.CraftBlock.at(p_258470_.level(), blockpos));
        p_258470_.level().getCraftServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            cir.setReturnValue(false);
        }
        // CraftBukkit end
    }
}
