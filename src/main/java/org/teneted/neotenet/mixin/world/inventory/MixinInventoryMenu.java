package org.teneted.neotenet.mixin.world.inventory;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import org.bukkit.craftbukkit.inventory.CraftInventoryCrafting;
import org.bukkit.craftbukkit.inventory.CraftInventoryView;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InventoryMenu.class)
public abstract class MixinInventoryMenu extends RecipeBookMenu<CraftingInput, CraftingRecipe> {

    @Mutable
    @Shadow
    @Final
    private CraftingContainer craftSlots;

    @Mutable
    @Shadow
    @Final
    private ResultContainer resultSlots;

    @Shadow
    @Final
    public Player owner;

    @Shadow
    public CraftInventoryView bukkitEntity;
    @Shadow
    private Inventory player;

    protected MixinInventoryMenu(@Nullable MenuType<?> menuType, int i) {
        super(menuType, i);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void neotenet$init(Inventory p_39706_, boolean p_39707_, Player p_39708_, CallbackInfo ci) {
        // CraftBukkit start
        this.resultSlots = new ResultContainer(); // CraftBukkit - moved to before InventoryCrafting construction
        this.craftSlots = new TransientCraftingContainer(this, 2, 2, p_39706_.player); // CraftBukkit - pass player
        // this.craftSlots.resultInventory = this.resultSlots; // CraftBukkit - let InventoryCrafting know about its result slot// TODO fixme
        this.player = p_39706_; // CraftBukkit - save player
        setTitle(Component.translatable("container.crafting")); // SPIGOT-4722: Allocate title for player inventory
        // CraftBukkit end
    }

    @Inject(method = "stillValid", cancellable = true, at = @At("HEAD"))
    public void neotenet$unreachable(Player playerIn, CallbackInfoReturnable<Boolean> cir) {
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
