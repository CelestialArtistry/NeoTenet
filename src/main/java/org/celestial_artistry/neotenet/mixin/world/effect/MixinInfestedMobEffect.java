package org.celestial_artistry.neotenet.mixin.world.effect;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Cancellable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.world.effect.InfestedMobEffect")
public class MixinInfestedMobEffect {

    @Redirect(method = "spawnSilverfish", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean neotenet$checkSpawn(Level instance, Entity entity, @Cancellable CallbackInfo ci) {
        // CraftBukkit start
        if (!instance.addFreshEntity(entity, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.POTION_EFFECT)) {
            ci.cancel();
            return false;
        }
        // CraftBukkit end
        return true;
    }
}
