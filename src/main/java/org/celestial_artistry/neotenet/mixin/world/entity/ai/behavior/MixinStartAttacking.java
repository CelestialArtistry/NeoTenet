package org.celestial_artistry.neotenet.mixin.world.entity.ai.behavior;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.StartAttacking;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryAccessor;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityTargetEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;
import java.util.function.Predicate;

@Mixin(StartAttacking.class)
public class MixinStartAttacking {

    @Inject(method = "lambda$create$1", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/behavior/declarative/MemoryAccessor;set(Ljava/lang/Object;)V"), cancellable = true)
    private static void neotenet$startTargetEvent(Predicate p_259618_, Function p_259435_, MemoryAccessor p_258778_, MemoryAccessor p_258779_, ServerLevel p_258773_, Mob p_258774_, long p_258775_, CallbackInfoReturnable<Boolean> cir, @Local(name = "livingentity") LivingEntity livingentity) {
        // CraftBukkit start
        EntityTargetEvent event = CraftEventFactory.callEntityTargetLivingEvent(p_258774_, livingentity, (livingentity instanceof ServerPlayer) ? EntityTargetEvent.TargetReason.CLOSEST_PLAYER : EntityTargetEvent.TargetReason.CLOSEST_ENTITY);
        if (event.isCancelled()) {
            cir.setReturnValue(false);
        }
        if (event.getTarget() == null) {
            p_258778_.erase();
            cir.setReturnValue(true);
        }
        livingentity = ((CraftLivingEntity) event.getTarget()).getHandle();
        // CraftBukkit end
    }
}
