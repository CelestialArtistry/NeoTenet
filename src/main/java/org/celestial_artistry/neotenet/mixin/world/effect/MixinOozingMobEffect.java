package org.celestial_artistry.neotenet.mixin.world.effect;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.world.effect.OozingMobEffect")
public class MixinOozingMobEffect {

    @Inject(method = "spawnSlimeOffspring", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private void neotenet$pushCause(Level p_338724_, double p_338555_, double p_338811_, double p_338192_, CallbackInfo ci, @Local(ordinal = 0) Slime slime) {
        slime.pushSpawnCause(org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.POTION_EFFECT); // CraftBukkit
    }
}
