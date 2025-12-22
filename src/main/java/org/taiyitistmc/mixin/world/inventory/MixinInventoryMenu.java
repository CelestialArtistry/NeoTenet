package org.taiyitistmc.mixin.world.inventory;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import org.bukkit.craftbukkit.inventory.CraftInventoryCrafting;
import org.bukkit.craftbukkit.inventory.CraftInventoryView;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InventoryMenu.class)
public abstract class MixinInventoryMenu extends RecipeBookMenu<CraftingInput, CraftingRecipe> {

    @Shadow
    @Final
    private CraftingContainer craftSlots;

    @Shadow
    @Final
    private ResultContainer resultSlots;

    @Shadow
    @Final
    public Player owner;

    @Shadow
    public CraftInventoryView bukkitEntity;

    protected MixinInventoryMenu(@Nullable MenuType<?> menuType, int i) {
        super(menuType, i);
    }

    @Inject(method = "stillValid", cancellable = true, at = @At("HEAD"))
    public void taiyitist$unreachable(Player playerIn, CallbackInfoReturnable<Boolean> cir) {
        if (!checkReachable) cir.setReturnValue(true);
    }

    @Override
    public CraftInventoryView getBukkitView() {
        if (bukkitEntity != null) {
            return bukkitEntity;
        }

        CraftInventoryCrafting inventory = new CraftInventoryCrafting(this.craftSlots, this.resultSlots);
        bukkitEntity = new CraftInventoryView(this.owner.getBukkitEntity(), inventory, (InventoryMenu) (Object) this);
        return bukkitEntity;
    }
}
