package org.celestial_artistry.neotenet.mixin.world.entity.ai.behavior;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.GoToWantedItem;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryAccessor;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(GoToWantedItem.class)
public class MixinGoToWantedItem {

    @Inject(method = "lambda$create$1", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/behavior/declarative/MemoryAccessor;set(Ljava/lang/Object;)V"), cancellable = true)
    private static void neotenet$gotoEvent(BehaviorBuilder.Instance p_258371_, MemoryAccessor p_258389_, MemoryAccessor p_258390_, Predicate p_259490_, int p_259054_, float p_260346_, MemoryAccessor p_258387_, MemoryAccessor p_258388_, ServerLevel p_258380_, LivingEntity p_258381_, long p_258382_, CallbackInfoReturnable<Boolean> cir, @Local(ordinal = 0) ItemEntity itementity) {
        // CraftBukkit start
        if (p_258381_ instanceof net.minecraft.world.entity.animal.allay.Allay) {
            org.bukkit.event.entity.EntityTargetEvent event = org.bukkit.craftbukkit.event.CraftEventFactory.callEntityTargetEvent(p_258381_, itementity, org.bukkit.event.entity.EntityTargetEvent.TargetReason.CLOSEST_ENTITY);

            if (event.isCancelled()) {
                cir.setReturnValue(false);
            }
            if (!(event.getTarget() instanceof ItemEntity)) {
                p_258389_.erase();
            }

            itementity = (ItemEntity) ((org.bukkit.craftbukkit.entity.CraftEntity) event.getTarget()).getHandle();
        }
        // CraftBukkit end
    }
}
