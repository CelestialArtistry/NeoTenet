package org.celestial_artistry.neotenet.mixin.world.item.enchantment.effects;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.effects.ReplaceDisk;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ReplaceDisk.class)
public class MixinReplaceDisk {

    @Redirect(method = "apply", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private boolean neotenet$fireBlockEvent(ServerLevel instance, BlockPos blockPos, BlockState blockState, @Local(argsOnly = true) Entity p_353038_) {
        return CraftEventFactory.handleBlockFormEvent(instance, blockPos,  blockState, p_353038_);
    }
}
