package org.teneted.neotenet.mixin.world.level.dimension.end;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(targets = "net/minecraft/world/level/dimension/end/DragonRespawnAnimation$4")
public class MixinDragonRespawnAnimation_4 {

    @Inject(method = "tick", at = @At("HEAD"))
    private void neotenet$explode(ServerLevel serverLevel, EndDragonFight endDragonFight, List<EndCrystal> list, int i, BlockPos blockPos, CallbackInfo ci) {
        if (i >= 100) {
            for (var endCrystal : list) {
                endCrystal.pushRemoveCause(EntityRemoveEvent.Cause.EXPLODE);
            }
        }
    }
}
