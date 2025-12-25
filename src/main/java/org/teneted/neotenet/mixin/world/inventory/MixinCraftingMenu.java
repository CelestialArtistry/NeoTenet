package org.teneted.neotenet.mixin.world.inventory;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RepairItemRecipe;
import net.minecraft.world.level.Level;
import org.bukkit.craftbukkit.inventory.CraftInventoryCrafting;
import org.bukkit.craftbukkit.inventory.CraftInventoryView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(CraftingMenu.class)
public abstract class MixinCraftingMenu extends RecipeBookMenu<CraftingInput, CraftingRecipe> {

    @Shadow
    private CraftInventoryView bukkitEntity;

    @Mutable
    @Shadow
    @Final
    private TransientCraftingContainer craftSlots;

    @Shadow
    @Final
    private Player player;

    @Shadow
    @Final
    public ResultContainer resultSlots;

    public MixinCraftingMenu(MenuType<?> p_40115_, int p_40116_) {
        super(p_40115_, p_40116_);
    }

    @Inject(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V", at = @At("RETURN"))
    private void neotenet$init(int p_39356_, Inventory p_39357_, ContainerLevelAccess p_39358_, CallbackInfo ci) {
        // CraftBukkit start - Switched order of IInventory construction and stored player
        this.craftSlots = new TransientCraftingContainer(this, 3, 3, p_39357_.player); // CraftBukkit - pass player
        this.craftSlots.resultInventory = this.resultSlots;
        // CraftBukkit end
    }

    @Inject(method = "slotChangedCraftingGrid", at = @At(value = "INVOKE", target = "Ljava/util/Optional;isPresent()Z"))
    private static void neotenet$setCurrentRecipe(AbstractContainerMenu p_150547_, Level p_150548_, Player p_150549_, CraftingContainer p_150550_, ResultContainer p_150551_, RecipeHolder<CraftingRecipe> p_345124_, CallbackInfo ci, @Local Optional<RecipeHolder<CraftingRecipe>> optional) {
        p_150550_.setCurrentRecipe(optional.orElse(null)); // CraftBukkit
    }

    @Inject(method = "slotChangedCraftingGrid", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/ResultContainer;setItem(ILnet/minecraft/world/item/ItemStack;)V"))
    private static void neotenet$callPreCraftEvent(AbstractContainerMenu p_150547_, Level p_150548_, Player p_150549_, CraftingContainer p_150550_, ResultContainer p_150551_, RecipeHolder<CraftingRecipe> p_345124_, CallbackInfo ci, @Local ItemStack itemstack, @Local Optional<RecipeHolder<CraftingRecipe>> optional) {
        itemstack = org.bukkit.craftbukkit.event.CraftEventFactory.callPreCraftEvent(p_150550_, p_150551_, itemstack, p_150547_.getBukkitView(), optional.map(RecipeHolder::value).orElse(null) instanceof RepairItemRecipe); // CraftBukkit
    }

    @Inject(method = "stillValid", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/CraftingMenu;stillValid(Lnet/minecraft/world/inventory/ContainerLevelAccess;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/block/Block;)Z"), cancellable = true)
    private void neotenet$checkValid(Player p_39368_, CallbackInfoReturnable<Boolean> cir) {
        if (!this.checkReachable) cir.setReturnValue(true); // CraftBukkit
    }

    // CraftBukkit start
    @Override
    public CraftInventoryView getBukkitView() {
        if (bukkitEntity != null) {
            return bukkitEntity;
        }

        CraftInventoryCrafting inventory = new CraftInventoryCrafting(this.craftSlots, this.resultSlots);
        bukkitEntity = new CraftInventoryView(this.player.getBukkitEntity(), inventory, this);
        return bukkitEntity;
    }
    // CraftBukkit end
}
