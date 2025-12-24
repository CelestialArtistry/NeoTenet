package org.teneted.neotenet.mixin.world.entity.ai.behavior;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.TryLaySpawnOnWaterNearLand;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TryLaySpawnOnWaterNearLand.class)
public class MixinTryLaySpawnOnWaterNearLand {

    @Inject(method = "lambda$create$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"), cancellable = true)
    private static void neotenet$callEntityChangeBlockEvent(Block p_259207_, MemoryAccessor p_260037_, ServerLevel p_269881_, LivingEntity p_269882_, long p_269883_, CallbackInfoReturnable<Boolean> cir, @Local(ordinal = 2) BlockPos blockpos2, @Local(ordinal = 0) BlockState blockstate) {
        // CraftBukkit start
        if (!org.bukkit.craftbukkit.event.CraftEventFactory.callEntityChangeBlockEvent(p_269882_, blockpos2, blockstate)) {
            p_260037_.erase();
            cir.setReturnValue(true);
        }
        // CraftBukkit end
    }
}
