package org.celestial_artistry.neotenet.mixin.world.item.enchantment.effects;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.effects.ReplaceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ReplaceBlock.class)
public class MixinReplaceBlock {

    @Shadow
    @Final
    private BlockStateProvider blockState;

    @Redirect(method = "apply", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private boolean neotenet$blockEvent(ServerLevel instance, BlockPos blockPos, BlockState blockState, @Local(argsOnly = true) Entity p_346293_) {
        return CraftEventFactory.handleBlockFormEvent(instance, blockPos, this.blockState.getState(p_346293_.getRandom(), blockPos), p_346293_);
    }
}
