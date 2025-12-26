package org.teneted.neotenet.mixin.world.entity;

import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.level.Level;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AreaEffectCloud.class)
public abstract class MixinAreaEffectCloud extends Entity implements TraceableEntity {

    public MixinAreaEffectCloud(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/AreaEffectCloud;discard()V"))
    private void neotenet$removeCause(CallbackInfo ci) {
        this.pushRemoveCause(EntityRemoveEvent.Cause.DESPAWN); // CraftBukkit - add Bukkit remove cause
    }
}
