package org.teneted.neotenet.mixin.world.level.block;

import com.llamalad7.mixinextras.sugar.Cancellable;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.player.PlayerHarvestBlockEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;

@Mixin(SweetBerryBushBlock.class)
public abstract class MixinSweetBerryBushBlock extends BushBlock {

    protected MixinSweetBerryBushBlock(Properties p_51021_) {
        super(p_51021_);
    }

    @Redirect(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private boolean neotenet$handleBlockGrowEvent(ServerLevel instance, BlockPos pos, BlockState blockState, int i, @Cancellable CallbackInfo ci) {
        boolean flag = !CraftEventFactory.handleBlockGrowEvent(instance, pos, blockState, i);
        if (flag) {
            ci.cancel();
        }
        return true;
    }

    @ModifyArg(method = "entityInside", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"), index = 0)
    private DamageSource neotenet$useBukkit(DamageSource p_19946_, @Local(argsOnly = true) Level p_57271_, @Local(argsOnly = true) BlockPos p_57272_) {
        return p_57271_.damageSources().sweetBerryBush().directBlock(p_57271_, p_57272_);
    }

    @Redirect(method = "useWithoutItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/SweetBerryBushBlock;popResource(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;)V"))
    private void neotenet$callPlayerHarvestBlockEvent(Level level, BlockPos pos, ItemStack itemStack, @Local(ordinal = 0) boolean flag, @Local(argsOnly = true) Player p_316431_, @Local(ordinal = 1) int j, @Cancellable CallbackInfoReturnable<InteractionResult> cir) {
        // CraftBukkit start - useWithoutItem is always MAIN_HAND
        PlayerHarvestBlockEvent event = CraftEventFactory.callPlayerHarvestBlockEvent(level, pos, p_316431_, InteractionHand.MAIN_HAND, Collections.singletonList(new ItemStack(Items.SWEET_BERRIES, j + (flag ? 1 : 0))));
        if (event.isCancelled()) {
            cir.setReturnValue(InteractionResult.SUCCESS); // We need to return a success either way, because making it PASS or FAIL will result in a bug where cancelling while harvesting w/ block in hand places block
        }
        for (org.bukkit.inventory.ItemStack bukkitStack : event.getItemsHarvested()) {
            popResource(level, pos, CraftItemStack.asNMSCopy(bukkitStack));
        }
        // CraftBukkit end
    }

}
