package org.teneted.neotenet.mixin.world.item;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.util.DummyGeneratorAccess;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BucketItem.class)
public class MixinBucketItem {

    @Unique
    PlayerBucketFillEvent event;

    @Inject(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/BucketPickup;pickupBlock(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/item/ItemStack;"), cancellable = true)
    private void neotenet$callPlayerBucketFillEvent(Level p_40703_, Player p_40704_, InteractionHand p_40705_, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir,
                                                    @Local BucketPickup bucketpicku, @Local(ordinal = 0) BlockPos blockpos,
                                                    @Local(ordinal = 0) BlockState blockstate1, @Local(ordinal = 0) BlockHitResult blockhitresult,
                                                    @Local(ordinal = 0) ItemStack itemstack) {
        // CraftBukkit start
        ItemStack dummyFluid = bucketpicku.pickupBlock(p_40704_, DummyGeneratorAccess.INSTANCE, blockpos, blockstate1);
        if (dummyFluid.isEmpty())
            cir.setReturnValue(InteractionResultHolder.fail(itemstack));// Don't fire event if the bucket won't be filled.
        event = CraftEventFactory.callPlayerBucketFillEvent((ServerLevel) p_40703_, p_40704_, blockpos, blockpos, blockhitresult.getDirection(), itemstack, dummyFluid.getItem(), p_40705_);

        if (event.isCancelled()) {
            ((ServerPlayer) p_40704_).connection.send(new ClientboundBlockUpdatePacket(p_40703_, blockpos)); // SPIGOT-5163 (see PlayerInteractManager)
            ((ServerPlayer) p_40704_).getBukkitEntity().updateInventory(); // SPIGOT-4541
            cir.setReturnValue(InteractionResultHolder.fail(itemstack));
        }
        // CraftBukkit end
    }

    @ModifyArg(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemUtils;createFilledResult(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/ItemStack;"), index = 2)
    private ItemStack neotenet$modifyPlayerBucketFillEvent(ItemStack p_41814_) {
        // CraftBukkit start
        return CraftItemStack.asNMSCopy(event.getItemStack());
    }

    @Inject(method = "emptyContents(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/BlockHitResult;Lnet/minecraft/world/item/ItemStack;)Z", at = @At(value = "INVOKE", target = "Ljava/util/Optional;ofNullable(Ljava/lang/Object;)Ljava/util/Optional;"), cancellable = true)
    private void neotenet$callPlayerBucketEmptyEvent(Player p_150716_, Level p_150717_, BlockPos p_150718_, BlockHitResult p_150719_, ItemStack container, CallbackInfoReturnable<Boolean> cir, @Local(ordinal = 1) boolean flag1) {
        // CraftBukkit start
        if (flag1 && p_150716_ != null) {
            PlayerBucketEmptyEvent event = CraftEventFactory.callPlayerBucketEmptyEvent((ServerLevel) p_150717_, p_150716_, p_150718_, p_150719_.getBlockPos(), p_150719_.getDirection(), container, p_150716_.getUsedItemHand());
            if (event.isCancelled()) {
                ((ServerPlayer) p_150716_).connection.send(new ClientboundBlockUpdatePacket(p_150717_, p_150718_)); // SPIGOT-4238: needed when looking through entity
                ((ServerPlayer) p_150716_).getBukkitEntity().updateInventory(); // SPIGOT-4541
                cir.setReturnValue(false);
            }
        }
        // CraftBukkit end
    }
}
