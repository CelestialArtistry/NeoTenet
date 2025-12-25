package org.teneted.neotenet.mixin.world.gameevent;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventDispatcher;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftGameEvent;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.event.world.GenericGameEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameEventDispatcher.class)
public class MixinGameEventDispatcher {

    @Shadow
    @Final
    private ServerLevel level;

    @Inject(method = "post", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/SectionPos;blockToSectionCoord(I)I", ordinal = 0), cancellable = true)
    private void neotenet$gameEvent(Holder<GameEvent> p_316151_, Vec3 p_250613_, GameEvent.Context p_251777_, CallbackInfo ci, @Local(ordinal = 0) int i, @Local(ordinal = 0) BlockPos blockpos) {
        // CraftBukkit start
        GenericGameEvent event = new GenericGameEvent(CraftGameEvent.minecraftToBukkit(p_316151_.value()), CraftLocation.toBukkit(blockpos, level.getWorld()), (p_251777_.sourceEntity() == null) ? null : p_251777_.sourceEntity().getBukkitEntity(), i, !Bukkit.isPrimaryThread());
        level.getCraftServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            ci.cancel();
            return;
        }
        i = event.getRadius();
        // CraftBukkit end
    }
}
