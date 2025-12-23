package org.celestial_artistry.neotenet.mixin.world.entity.ai.goal;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.EatBlockGoal;
import net.minecraft.world.level.block.Blocks;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EatBlockGoal.class)
public class MixinEatBlockGoal {

    @Shadow
    @Final
    private Mob mob;

    @ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/event/EventHooks;canEntityGrief(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/Entity;)Z", ordinal = 0))
    private boolean neotenet$callEntityChangeBlockEvent(boolean original, @Local(ordinal = 0) BlockPos blockpos) {
        return CraftEventFactory.callEntityChangeBlockEvent(this.mob, blockpos, Blocks.AIR.defaultBlockState(), !original);
    }

    @ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/event/EventHooks;canEntityGrief(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/Entity;)Z", ordinal = 1))
    private boolean neotenet$callEntityChangeBlockEvent0(boolean original, @Local(ordinal = 1) BlockPos blockpos1) {
        return CraftEventFactory.callEntityChangeBlockEvent(this.mob, blockpos1, Blocks.AIR.defaultBlockState(), !original);
    }
}
