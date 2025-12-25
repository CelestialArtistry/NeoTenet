package org.teneted.neotenet.mixin.world.level.block.entity.vault;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.vault.VaultBlockEntity;
import net.minecraft.world.level.block.entity.vault.VaultConfig;
import net.minecraft.world.level.block.entity.vault.VaultServerData;
import net.minecraft.world.level.block.entity.vault.VaultSharedData;
import net.minecraft.world.level.block.entity.vault.VaultState;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.block.BlockDispenseLootEvent;
import org.bukkit.event.block.VaultDisplayItemEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(VaultBlockEntity.Server.class)
public class MixinVaultBlockEntity_Server {

    @Inject(method = "tryInsertKey", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/vault/VaultBlockEntity$Server;unlock(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/vault/VaultConfig;Lnet/minecraft/world/level/block/entity/vault/VaultServerData;Lnet/minecraft/world/level/block/entity/vault/VaultSharedData;Ljava/util/List;)V"), cancellable = true)
    private static void neotenet$vaultDispenseLootEvent(ServerLevel p_323533_, BlockPos p_323777_, BlockState p_324589_, VaultConfig p_323660_, VaultServerData p_323829_, VaultSharedData p_324341_, Player p_324373_, ItemStack p_324551_, CallbackInfo ci, @Local List<ItemStack> list) {
        // CraftBukkit start
        BlockDispenseLootEvent vaultDispenseLootEvent = CraftEventFactory.callBlockDispenseLootEvent(p_323533_, p_323777_, p_324373_, list);
        if (vaultDispenseLootEvent.isCancelled()) {
            ci.cancel();
            return;
        }

        list = vaultDispenseLootEvent.getDispensedLoot().stream().map(CraftItemStack::asNMSCopy).toList();
        // CraftBukkit end
    }

    @Inject(method = "cycleDisplayItemFromLootTable", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/vault/VaultSharedData;setDisplayItem(Lnet/minecraft/world/item/ItemStack;)V", ordinal = 1), cancellable = true)
    private static void neotenet$callVaultDisplayItemEvent(ServerLevel p_323551_, VaultState p_324221_, VaultConfig p_324332_, VaultSharedData p_323644_, BlockPos p_323602_, CallbackInfo ci, @Local ItemStack itemstack) {
        // CraftBukkit start
        VaultDisplayItemEvent event = CraftEventFactory.callVaultDisplayItemEvent(p_323551_, p_323602_, itemstack);
        if (event.isCancelled()) {
            ci.cancel();
            return;
        }

        itemstack = CraftItemStack.asNMSCopy(event.getDisplayItem());
        // CraftBukkit end
    }
}
