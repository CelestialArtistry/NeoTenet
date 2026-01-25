package org.teneted.neotenet.mixin.world.level.block;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.block.TNTPrimeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TntBlock.class)
public class MixinTntBlock {

    @ModifyExpressionValue(method = "onPlace", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;hasNeighborSignal(Lnet/minecraft/core/BlockPos;)Z"))
    private boolean neotenet$callTNTPrimeEvent(boolean original, @Local(argsOnly = true) Level p_57467_, @Local(argsOnly = true) BlockPos p_57468_) {
        return original && CraftEventFactory.callTNTPrimeEvent(p_57467_, p_57468_, TNTPrimeEvent.PrimeCause.REDSTONE, null, null);
    }

    @ModifyExpressionValue(method = "neighborChanged", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;hasNeighborSignal(Lnet/minecraft/core/BlockPos;)Z"))
    private boolean neotenet$callTNTPrimeEvent0(boolean original, @Local(argsOnly = true) Level p_57467_, @Local(argsOnly = true, ordinal = 0) BlockPos p_57468_, @Local(argsOnly = true, ordinal = 1) BlockPos p_57461_) {
        return original && CraftEventFactory.callTNTPrimeEvent(p_57467_, p_57468_, TNTPrimeEvent.PrimeCause.REDSTONE, null, p_57461_);
    }

    @ModifyExpressionValue(method = "playerWillDestroy", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isClientSide()Z"))
    private boolean neotenet$callTNTPrimeEvent1(boolean original, @Local(argsOnly = true) Level p_57467_, @Local(argsOnly = true, ordinal = 0) BlockPos p_57468_, @Local(argsOnly = true) Player p_57448_) {
        return original && CraftEventFactory.callTNTPrimeEvent(p_57467_, p_57468_, TNTPrimeEvent.PrimeCause.BLOCK_BREAK, p_57448_, null);
    }

    @Inject(method = "useItemOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/TntBlock;onCaughtFire(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;Lnet/minecraft/world/entity/LivingEntity;)V"), cancellable = true)
    private void neotenet$callTNTPrimeEvent2(ItemStack p_316149_, BlockState p_316217_, Level p_316520_, BlockPos p_316601_, Player p_316770_, InteractionHand p_316393_, BlockHitResult p_316532_, CallbackInfoReturnable<ItemInteractionResult> cir) {
        // CraftBukkit start - TNTPrimeEvent
        if (!CraftEventFactory.callTNTPrimeEvent(p_316520_, p_316601_, TNTPrimeEvent.PrimeCause.PLAYER, p_316770_, null)) {
            cir.setReturnValue(ItemInteractionResult.CONSUME);
        }
        // CraftBukkit end
    }

    @Inject(method = "onProjectileHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/TntBlock;onCaughtFire(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;Lnet/minecraft/world/entity/LivingEntity;)V"), cancellable = true)
    private void neotenet$callEntityChangeBlockEvent(Level p_57429_, BlockState p_57430_, BlockHitResult p_57431_, Projectile p_57432_, CallbackInfo ci, @Local(ordinal = 0) BlockPos blockpos) {
        // CraftBukkit start
        if (!org.bukkit.craftbukkit.event.CraftEventFactory.callEntityChangeBlockEvent(p_57432_, blockpos, Blocks.AIR.defaultBlockState()) || !CraftEventFactory.callTNTPrimeEvent(p_57429_, blockpos, TNTPrimeEvent.PrimeCause.PROJECTILE, p_57432_, null)) {
            ci.cancel();
            return;
        }
        // CraftBukkit end
    }
}
