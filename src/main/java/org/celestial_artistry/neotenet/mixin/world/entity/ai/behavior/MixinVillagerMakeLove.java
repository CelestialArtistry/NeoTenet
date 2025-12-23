package org.celestial_artistry.neotenet.mixin.world.entity.ai.behavior;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.VillagerMakeLove;
import net.minecraft.world.entity.npc.Villager;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(VillagerMakeLove.class)
public class MixinVillagerMakeLove {

    @Redirect(method = "breed", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/npc/Villager;setAge(I)V", ordinal = 0))
    private void neotenet$cancelSetAge(Villager instance, int i) { }

    @Redirect(method = "breed", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/npc/Villager;setAge(I)V", ordinal = 1))
    private void neotenet$cancelSetAge0(Villager instance, int i) { }


    @Inject(method = "breed", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntityWithPassengers(Lnet/minecraft/world/entity/Entity;)V"), cancellable = true)
    private void neotenet$makeLoveEvent(ServerLevel p_24656_, Villager p_24657_, Villager p_24658_, CallbackInfoReturnable<Optional<Villager>> cir, @Local(ordinal = 1) Villager villager) {
        // CraftBukkit start - call EntityBreedEvent
        if (CraftEventFactory.callEntityBreedEvent(villager, p_24658_, p_24657_, null, null, 0).isCancelled()) {
            cir.setReturnValue(Optional.empty());
        }
        // Move age setting down
        p_24657_.setAge(6000);
        p_24658_.setAge(6000);
    }

}
