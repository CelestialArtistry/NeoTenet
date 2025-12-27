package org.teneted.neotenet.mixin.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.event.entity.EntityExhaustionEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.teneted.neotenet.injection.world.level.block.InjectionBlock;

@Mixin(Block.class)
public abstract class MixinBlock implements InjectionBlock {

    @Redirect(method = "popResource(Lnet/minecraft/world/level/Level;Ljava/util/function/Supplier;Lnet/minecraft/world/item/ItemStack;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private static boolean neotenet$captureDrops(Level instance, Entity entity) {
        // CraftBukkit start
        if (instance.captureDrops != null) {
            instance.captureDrops.add((ItemEntity) entity);
        } else {
            instance.addFreshEntity(entity);
        }
        // CraftBukkit end
        return true;
    }

    @Inject(method = "playerDestroy", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;causeFoodExhaustion(F)V"))
    private void neotenet$pushExhaustReason(Level p_49827_, Player p_49828_, BlockPos p_49829_, BlockState p_49830_, BlockEntity p_49831_, ItemStack p_49832_, CallbackInfo ci) {
        p_49828_.pushExhaustReason(EntityExhaustionEvent.ExhaustionReason.BLOCK_MINED);
    }

    @Override
    public int getExpDrop(BlockState blockState, ServerLevel world, BlockPos blockPos, ItemStack itemStack, boolean flag) {
        return 0;
    }


    @Override
    public int neotenet$tryDropExperience(ServerLevel level, BlockPos pos, ItemStack heldItem, IntProvider amount) {
        int i = EnchantmentHelper.processBlockExperience(level, heldItem, amount.sample(level.getRandom()));
        return i;
    }
}
