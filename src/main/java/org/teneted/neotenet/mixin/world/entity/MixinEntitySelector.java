package org.teneted.neotenet.mixin.world.entity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

@Mixin(EntitySelector.class)
public class MixinEntitySelector {

    @Unique
    private static AtomicReference<Entity> neotenet$entity = new AtomicReference<>(null);

    @Inject(method = "pushableBy", at = @At("HEAD"))
    private static void neotenet$getEntity(Entity p_20422_, CallbackInfoReturnable<Predicate<Entity>> cir) {
        neotenet$entity.set(p_20422_);
    }

    @ModifyExpressionValue(method = "lambda$pushableBy$6", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;isPushable()Z"))
    private static boolean neotenet$collidable(boolean original, @Local(ordinal = 0) Entity p_20430_) {
        return p_20430_.canCollideWithBukkit(neotenet$entity.get()) || !neotenet$entity.get().canCollideWithBukkit(p_20430_);
    }
}
