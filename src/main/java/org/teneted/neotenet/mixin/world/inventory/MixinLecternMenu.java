package org.teneted.neotenet.mixin.world.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.LecternMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.inventory.CraftInventoryLectern;
import org.bukkit.craftbukkit.inventory.view.CraftLecternView;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.player.PlayerTakeLecternBookEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LecternMenu.class)
public abstract class MixinLecternMenu extends AbstractContainerMenu {

    @Shadow
    private CraftLecternView bukkitEntity;

    @Shadow
    private HumanEntity player;

    @Shadow
    @Final
    private Container lectern;

    protected MixinLecternMenu(@Nullable MenuType<?> p_38851_, int p_38852_) {
        super(p_38851_, p_38852_);
    }

    @Inject(method = "clickMenuButton", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/Container;removeItemNoUpdate(I)Lnet/minecraft/world/item/ItemStack;"), cancellable = true)
    private void neotenet$callPlayerTakeLecternBookEvent(Player p_39833_, int p_39834_, CallbackInfoReturnable<Boolean> cir) {
        // CraftBukkit start - Event for taking the book
        PlayerTakeLecternBookEvent event = new PlayerTakeLecternBookEvent((org.bukkit.entity.Player) p_39833_.getBukkitEntity(), ((CraftInventoryLectern) getBukkitView().getTopInventory()).getHolder());
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            cir.setReturnValue(false);
        }
        // CraftBukkit end
    }

    @Inject(method = "stillValid", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/Container;stillValid(Lnet/minecraft/world/entity/player/Player;)Z"), cancellable = true)
    private void neotenet$checkInv(Player p_39831_, CallbackInfoReturnable<Boolean> cir) {
        if (lectern instanceof LecternBlockEntity.LecternInventory && !((LecternBlockEntity.LecternInventory) lectern).getLectern().hasBook()) cir.setReturnValue(false); // CraftBukkit
        if (!this.checkReachable) cir.setReturnValue(true); // CraftBukkit
    }

    @Override
    public CraftLecternView getBukkitView() {
        if (bukkitEntity != null) {
            return bukkitEntity;
        }

        CraftInventoryLectern inventory = new CraftInventoryLectern(this.lectern);
        bukkitEntity = new CraftLecternView(this.player, inventory, ((LecternMenu) (Object) this));
        return bukkitEntity;
    }
    // CraftBukkit end
}
