package org.teneted.neotenet.mixin.world.level.block.entity;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Cancellable;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.block.BrewingStartEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.BrewingStandFuelEvent;
import org.bukkit.inventory.InventoryHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Mixin(BrewingStandBlockEntity.class)
public abstract class MixinBrewingStandBlockEntity extends BaseContainerBlockEntity {
    @Shadow
    private int lastTick;
    @Shadow
    public List<HumanEntity> transaction;
    @Shadow
    private int maxStack;
    @Shadow
    private NonNullList<ItemStack> items;

    @Shadow
    protected static void doBrew(Level p_155291_, BlockPos p_155292_, NonNullList<ItemStack> p_155293_) {
    }

    protected MixinBrewingStandBlockEntity(BlockEntityType<?> p_155076_, BlockPos p_155077_, BlockState p_155078_) {
        super(p_155076_, p_155077_, p_155078_);
    }

    public void onOpen(CraftHumanEntity who) {
        transaction.add(who);
    }

    public void onClose(CraftHumanEntity who) {
        transaction.remove(who);
    }

    public List<HumanEntity> getViewers() {
        return transaction;
    }

    public List<ItemStack> getContents() {
        return this.items;
    }

    @Override
    public int getMaxStackSize() {
        return maxStack;
    }

    public void setMaxStackSize(int size) {
        maxStack = size;
    }
    // CraftBukkit end

    @WrapWithCondition(method = "serverTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V"))
    private static boolean neotenet$callBrewingStandFuelEvent(ItemStack instance, int i,
                                                              @Local BrewingStandBlockEntity p_155289_,
                                                              @Local Level p_155286_,
                                                              @Local BlockPos p_155287_,
                                                              @Cancellable CallbackInfo ci) {
        // CraftBukkit start
        BrewingStandFuelEvent event = new BrewingStandFuelEvent(CraftBlock.at(p_155286_, p_155287_), CraftItemStack.asCraftMirror(instance), 20);
        p_155286_.getCraftServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            ci.cancel();
        }
        p_155289_.fuel = event.getFuelPower();
        return p_155289_.fuel > 0 && event.isConsuming();
    }

    @Inject(method = "serverTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/BrewingStandBlockEntity;doBrew(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/NonNullList;)V"))
    private static void neotenet$pushBrewingStandBlockEntity(Level p_155286_, BlockPos p_155287_, BlockState p_155288_, BrewingStandBlockEntity p_155289_, CallbackInfo ci) {
        blockEntityAtomicReference.set(p_155289_);
    }

    @Redirect(method = "serverTick", at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/block/entity/BrewingStandBlockEntity;brewTime:I", ordinal = 4))
    private static void neotenet$callBrewingStartEvent(BrewingStandBlockEntity instance, int value,
                                                       @Local(ordinal = 1) ItemStack itemstack1,
                                                       @Local(argsOnly = true) Level p_155286_, @Local(argsOnly = true) BlockPos p_155287_) {
        // CraftBukkit start
        BrewingStartEvent event = new BrewingStartEvent(CraftBlock.at(p_155286_, p_155287_), CraftItemStack.asCraftMirror(itemstack1), 400);
        instance.getLevel().getCraftServer().getPluginManager().callEvent(event);
        instance.brewTime = event.getTotalBrewTime(); // 400 -> event.getTotalBrewTime()
        // CraftBukkit end
    }

    private static AtomicReference<BrewingStandBlockEntity> blockEntityAtomicReference = new AtomicReference<>();

    private static void doBrew(Level p_155291_, BlockPos p_155292_, NonNullList<ItemStack> p_155293_, BrewingStandBlockEntity tileentitybrewingstand) { // CraftBukkit
        blockEntityAtomicReference.set(tileentitybrewingstand);
        doBrew(p_155291_, p_155292_, p_155293_);
    }

    @Redirect(method = "doBrew", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/NonNullList;set(ILjava/lang/Object;)Ljava/lang/Object;"))
    private static Object neotenet$cancelAdd(NonNullList instance, int i, Object o) { return null; }

    @Inject(method = "doBrew", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/event/EventHooks;onPotionBrewed(Lnet/minecraft/core/NonNullList;)V"))
    private static void neotenet$callBrewEvent(Level p_155291_, BlockPos p_155292_, NonNullList<ItemStack> p_155293_, CallbackInfo ci) {
        InventoryHolder owner = blockEntityAtomicReference.get().getOwner();
        List<org.bukkit.inventory.ItemStack> brewResults = new ArrayList<>(3);
        for (int i = 0; i < 3; ++i) {
            brewResults.add(i, CraftItemStack.asCraftMirror(p_155291_.potionBrewing().mix(p_155293_.get(3), (ItemStack) p_155293_.get(i))));
        }

        if (owner != null) {
            BrewEvent event = new BrewEvent(CraftBlock.at(p_155291_, p_155292_), (org.bukkit.inventory.BrewerInventory) owner.getInventory(), brewResults, blockEntityAtomicReference.get().fuel);
            org.bukkit.Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return;
            }
        }
        // CraftBukkit end

        for (int i = 0; i < 3; ++i) {
            // CraftBukkit start - validate index in case it is cleared by plugins
            if (i < brewResults.size()) {
                p_155293_.set(i, CraftItemStack.asNMSCopy(brewResults.get(i)));
            } else {
                p_155293_.set(i, ItemStack.EMPTY);
            }
            // CraftBukkit end
        }
    }
}