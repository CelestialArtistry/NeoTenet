package org.teneted.neotenet.mixin.world.entity.ai.goal;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.ai.goal.RunAroundLikeCrazyGoal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RunAroundLikeCrazyGoal.class)
public class MixinRunAroundLikeCrazyGoal {

    @Shadow
    @Final
    private AbstractHorse horse;

    @ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/event/EventHooks;onAnimalTame(Lnet/minecraft/world/entity/animal/Animal;Lnet/minecraft/world/entity/player/Player;)Z"))
    private boolean neotenet$callEntityTameEvent(boolean original) {
        return original && !CraftEventFactory.callEntityTameEvent(this.horse, ((CraftHumanEntity) this.horse.getBukkitEntity().getPassenger()).getHandle()).isCancelled();
    }
}
