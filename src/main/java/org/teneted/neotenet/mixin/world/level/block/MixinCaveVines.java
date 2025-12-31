package org.teneted.neotenet.mixin.world.level.block;

import com.llamalad7.mixinextras.sugar.Cancellable;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CaveVines;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.player.PlayerHarvestBlockEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Collections;

@Mixin(CaveVines.class)
public interface MixinCaveVines {

    @Redirect(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;popResource(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;)V"))
    private static void neotenet$callPlayerHarvestBlockEvent(Level level, BlockPos blockPos, ItemStack itemStack, @Local(argsOnly = true) @Nullable Entity entity, @Local(argsOnly = true) BlockState iblockdata, @Cancellable CallbackInfoReturnable<InteractionResult> cir) {
        // CraftBukkit start
        if (!org.bukkit.craftbukkit.event.CraftEventFactory.callEntityChangeBlockEvent(entity, blockPos, (BlockState) iblockdata.setValue(CaveVines.BERRIES, false))) {
            cir.setReturnValue(InteractionResult.SUCCESS);
        }

        if (entity instanceof Player) {
            PlayerHarvestBlockEvent event = CraftEventFactory.callPlayerHarvestBlockEvent(level, blockPos, (Player) entity, net.minecraft.world.InteractionHand.MAIN_HAND, Collections.singletonList(new ItemStack(Items.GLOW_BERRIES, 1)));
            if (event.isCancelled()) {
                cir.setReturnValue(InteractionResult.SUCCESS); // We need to return a success either way, because making it PASS or FAIL will result in a bug where cancelling while harvesting w/ block in hand places block
            }
            for (org.bukkit.inventory.ItemStack bukkitStack : event.getItemsHarvested()) {
                Block.popResource(level, blockPos, CraftItemStack.asNMSCopy(bukkitStack));
            }
        } else {
            Block.popResource(level, blockPos, new ItemStack(Items.GLOW_BERRIES, 1));
        }
        // CraftBukkit end
    }
}
