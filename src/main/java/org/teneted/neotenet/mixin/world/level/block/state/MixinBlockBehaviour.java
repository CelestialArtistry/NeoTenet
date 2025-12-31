package org.teneted.neotenet.mixin.world.level.block.state;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockBehaviour.class)
public abstract class MixinBlockBehaviour {

    @Definition(id = "getBlockInteraction", method = "Lnet/minecraft/world/level/Explosion;getBlockInteraction()Lnet/minecraft/world/level/Explosion$BlockInteraction;")
    @Definition(id = "p_312925_", local = @Local(type = Explosion.class, argsOnly = true))
    @Definition(id = "DESTROY_WITH_DECAY", field = "Lnet/minecraft/world/level/Explosion$BlockInteraction;DESTROY_WITH_DECAY:Lnet/minecraft/world/level/Explosion$BlockInteraction;")
    @Expression(value = "p_312925_.getBlockInteraction() == DESTROY_WITH_DECAY")
    @ModifyExpressionValue(method = "onExplosionHit", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean neotenet$(boolean original, @Local(argsOnly = true) Explosion p_312925_) {
        return p_312925_.yield < 1.0F;
    }

    @Redirect(method = "onExplosionHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Explosion;radius()F"))
    private float neotenet$resetRadius(Explosion instance) {
        return 1.0F / instance.yield;
    }
}
