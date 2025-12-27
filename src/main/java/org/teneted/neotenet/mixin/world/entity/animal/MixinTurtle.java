package org.teneted.neotenet.mixin.world.entity.animal;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Turtle.class)
public abstract class MixinTurtle extends Animal {

    protected MixinTurtle(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "ageBoundaryReached", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/Turtle;spawnAtLocation(Lnet/minecraft/world/level/ItemLike;I)Lnet/minecraft/world/entity/item/ItemEntity;"))
    private void neotenet$forceDrop(CallbackInfo ci) {
        this.forceDrops = true;
    }

    @Inject(method = "ageBoundaryReached", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/world/entity/animal/Turtle;spawnAtLocation(Lnet/minecraft/world/level/ItemLike;I)Lnet/minecraft/world/entity/item/ItemEntity;"))
    private void neotenet$forceDropReset(CallbackInfo ci) {
        this.forceDrops = false;
    }

    @ModifyArg(method = "thunderHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/Turtle;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"), index = 0)
    private DamageSource neotenet$userThunderHit(DamageSource par1, @Local(argsOnly = true) LightningBolt p_30141_) {
        return par1.customEntityDamager(p_30141_);
    }
}
