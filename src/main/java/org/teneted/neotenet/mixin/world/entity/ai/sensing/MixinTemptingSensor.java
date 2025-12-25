package org.teneted.neotenet.mixin.world.entity.ai.sensing;

import com.llamalad7.mixinextras.sugar.Cancellable;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.TemptingSensor;
import net.minecraft.world.entity.player.Player;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TemptingSensor.class)
public class MixinTemptingSensor {

    @Redirect(method = "doTick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/PathfinderMob;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/Brain;setMemory(Lnet/minecraft/world/entity/ai/memory/MemoryModuleType;Ljava/lang/Object;)V"))
    private <U> void neotenet$callEntityTargetLivingEvent(Brain instance, MemoryModuleType<U> p_21880_, U p_21881_, @Local(argsOnly = true) PathfinderMob p_148332_, @Local Player player, @Cancellable CallbackInfo ci) {
        instance.setMemory(MemoryModuleType.TEMPTING_PLAYER, (Object) player);
        // CraftBukkit start
        EntityTargetLivingEntityEvent event = CraftEventFactory.callEntityTargetLivingEvent(p_148332_, player, EntityTargetEvent.TargetReason.TEMPT);
        if (event.isCancelled()) {
            ci.cancel();
            return;
        }
        if (event.getTarget() instanceof HumanEntity) {
            instance.setMemory(MemoryModuleType.TEMPTING_PLAYER, ((CraftHumanEntity) event.getTarget()).getHandle());
        } else {
            instance.eraseMemory(MemoryModuleType.TEMPTING_PLAYER);
        }
        // CraftBukkit end
    }
}
