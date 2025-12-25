package org.teneted.neotenet.mixin.world.level.block.entity;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(ChiseledBookShelfBlockEntity.class)
public abstract class MixinChiseledBookShelfBlockEntity extends BlockEntity implements Container {

    // CraftBukkit start - add fields and methods
    @Shadow
    public List<HumanEntity> transaction;
    @Shadow
    private int maxStack;

    @Shadow
    @Final
    private NonNullList<ItemStack> items;

    public MixinChiseledBookShelfBlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }


    @Override
    public List<ItemStack> getContents() {
        return this.items;
    }

    @Override
    public void onOpen(CraftHumanEntity who) {
        transaction.add(who);
    }

    @Override
    public void onClose(CraftHumanEntity who) {
        transaction.remove(who);
    }

    @Override
    public List<HumanEntity> getViewers() {
        return transaction;
    }

    @Override
    public void setMaxStackSize(int size) {
        maxStack = size;
    }

    @Override
    public Location getLocation() {
        if (level == null) return null;
        return new org.bukkit.Location(level.getWorld(), worldPosition.getX(), worldPosition.getY(), worldPosition.getZ());
    }
    // CraftBukkit end

    @Override
    public int getMaxStackSize() {
        return maxStack; // CraftBukkit
    }

    @WrapWithCondition(method = "removeItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/ChiseledBookShelfBlockEntity;updateState(I)V"))
    private boolean neotenet$checkIfNull(ChiseledBookShelfBlockEntity instance, int booleanproperty) {
        return level != null;
    }

    @WrapWithCondition(method = "setItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/ChiseledBookShelfBlockEntity;updateState(I)V"))
    private boolean neotenet$checkIfNull0(ChiseledBookShelfBlockEntity instance, int booleanproperty) {
        return level != null;
    }
}
