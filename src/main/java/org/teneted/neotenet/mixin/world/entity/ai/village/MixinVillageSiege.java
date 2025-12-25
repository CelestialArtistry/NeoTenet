package org.teneted.neotenet.mixin.world.entity.ai.village;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.village.VillageSiege;
import net.minecraft.world.entity.monster.Zombie;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VillageSiege.class)
public class MixinVillageSiege {

    @Inject(method = "trySpawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntityWithPassengers(Lnet/minecraft/world/entity/Entity;)V"))
    private void neotenet$pushSpawnReason(ServerLevel p_27017_, CallbackInfo ci, @Local Zombie zombie) {
        zombie.pushSpawnCause(CreatureSpawnEvent.SpawnReason.VILLAGE_INVASION);
    }
}
