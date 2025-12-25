package org.teneted.neotenet.mixin.world.gameevent.vibration;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.CraftGameEvent;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.event.block.BlockReceiveGameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(VibrationSystem.Listener.class)
public interface MixinVibrationSystem_Listener {

    @ModifyExpressionValue(method = "handleGameEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/gameevent/vibrations/VibrationSystem$User;canReceiveVibration(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Holder;Lnet/minecraft/world/level/gameevent/GameEvent$Context;)Z"))
    private boolean neotenet$handleGameEvent(boolean original, @Local(argsOnly = true) ServerLevel p_282254_, @Local(argsOnly = true) Holder<GameEvent> p_316513_, @Local(argsOnly = true) GameEvent.Context p_283664_, @Local(argsOnly = true) Vec3 p_282426_) {
        // CraftBukkit start
        Entity entity = p_283664_.sourceEntity();
        BlockReceiveGameEvent event = new BlockReceiveGameEvent(CraftGameEvent.minecraftToBukkit(p_316513_.value()), CraftBlock.at(p_282254_, BlockPos.containing(p_282426_)), (entity == null) ? null : entity.getBukkitEntity());
        event.setCancelled(original);
        p_282254_.getCraftServer().getPluginManager().callEvent(event);
        return !event.isCancelled();
    }
}
