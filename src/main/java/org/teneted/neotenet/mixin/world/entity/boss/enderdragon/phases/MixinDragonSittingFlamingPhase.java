package org.teneted.neotenet.mixin.world.entity.boss.enderdragon.phases;

import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonSittingFlamingPhase;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(DragonSittingFlamingPhase.class)
public class MixinDragonSittingFlamingPhase {

    @Shadow
    @Nullable
    private AreaEffectCloud flame;

    @Inject(method = "end", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/AreaEffectCloud;discard()V"))
    private void neotenet$pushRemoveCause(CallbackInfo ci) {
        this.flame.pushRemoveCause(EntityRemoveEvent.Cause.DESPAWN);
    }
}
