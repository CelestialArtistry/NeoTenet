package org.taiyitistmc.mixin.world.entity.moster;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(targets = "net.minecraft.world.entity.monster.EnderMan$EndermanTakeBlockGoal")
public class MixinEnderMan_EndermanTakeBlockGoal {

    // @formatter:off
    @Shadow @Final private EnderMan enderman;
    // @formatter:on

    @Inject(method = "tick", cancellable = true,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/EnderMan;setCarriedBlock(Lnet/minecraft/world/level/block/state/BlockState;)V"))
    private void taiyitist$entityChangeBlock(CallbackInfo ci, @Local BlockPos blockPos) {
        if (!CraftEventFactory.callEntityChangeBlockEvent(this.enderman, blockPos, Blocks.AIR.defaultBlockState())) {
            ci.cancel();
        }
    }
}
