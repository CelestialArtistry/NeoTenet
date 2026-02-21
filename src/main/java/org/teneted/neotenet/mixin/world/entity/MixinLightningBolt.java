package org.teneted.neotenet.mixin.world.entity;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightningBolt.class)
public abstract class MixinLightningBolt extends Entity {

    @Shadow
    private boolean visualOnly;

    public MixinLightningBolt(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LightningBolt;discard()V"))
    private void neotenet$discardReason(CallbackInfo ci) {
        this.pushRemoveCause(EntityRemoveEvent.Cause.DESPAWN);
    }

    @Definition(id = "life", field = "Lnet/minecraft/world/entity/LightningBolt;life:I")
    @Expression("this.life >= 0")
    @ModifyExpressionValue(method = "tick", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean neotenet$checkNonVisualOnly(boolean original) {
        return original && !this.visualOnly;
    }

    @ModifyExpressionValue(method = "spawnFire", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;canSurvive(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;)Z", ordinal = 0))
    private boolean neotenet$callBlockIgniteEvent(boolean original, @Local BlockPos blockpos) {
        return original && !visualOnly && !CraftEventFactory.callBlockIgniteEvent(this.level(), blockpos, this).isCancelled();
    }

    @ModifyExpressionValue(method = "spawnFire", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;canSurvive(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;)Z", ordinal = 1))
    private boolean neotenet$callBlockIgniteEvent0(boolean original, @Local(ordinal = 1) BlockPos blockpos) {
        return original && !visualOnly && !CraftEventFactory.callBlockIgniteEvent(this.level(), blockpos, this).isCancelled();
    }
}
