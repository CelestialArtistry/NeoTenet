package org.celestial_artistry.neotenet.mixin.world.entity.ai.behavior;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.StopAttackingIfTargetInvalid;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryAccessor;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityTargetEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

@Mixin(StopAttackingIfTargetInvalid.class)
public class MixinStopAttackingIfTargetInvalid {

    @Inject(method = "lambda$create$4", at = @At(value = "INVOKE", target = "Ljava/util/function/BiConsumer;accept(Ljava/lang/Object;Ljava/lang/Object;)V"), cancellable = true)
    private static void neotenet$targetEvent(BehaviorBuilder.Instance p_258801_, MemoryAccessor p_258787_, boolean p_260319_, MemoryAccessor p_258788_, Predicate p_260357_, BiConsumer p_259568_, ServerLevel p_258795_, Mob p_258796_, long p_258797_, CallbackInfoReturnable<Boolean> cir) {
        // CraftBukkit start
        LivingEntity old = p_258796_.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
        EntityTargetEvent event = CraftEventFactory.callEntityTargetLivingEvent(p_258796_, null, (old != null && !old.isAlive()) ? EntityTargetEvent.TargetReason.TARGET_DIED : EntityTargetEvent.TargetReason.FORGOT_TARGET);
        if (event.isCancelled()) {
            cir.setReturnValue(false);
        }
        if (event.getTarget() != null) {
            p_258796_.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, ((CraftLivingEntity) event.getTarget()).getHandle());
            cir.setReturnValue(true);
        }
        // CraftBukkit end
    }
}
