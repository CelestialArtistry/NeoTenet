package org.teneted.neotenet.mixin.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.EndGatewayBlock;
import net.minecraft.world.level.portal.DimensionTransition;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EndGatewayBlock.class)
public class MixinEndGatewayBlock {

    @Inject(method = "getPortalDestination", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/TheEndGatewayBlockEntity;getPortalPosition(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/Vec3;"))
    private void neotenet$pushTpCause(ServerLevel p_350958_, Entity p_350650_, BlockPos p_350525_, CallbackInfoReturnable<DimensionTransition> cir) {
        if (p_350650_ instanceof ServerPlayer serverPlayer) {
            serverPlayer.pushChangeDimensionCause(PlayerTeleportEvent.TeleportCause.END_GATEWAY);
        }
    }
}
