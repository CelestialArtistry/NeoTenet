package org.teneted.neotenet.mixin.world.level.block.entity;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Clearable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.JukeboxSongPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.ticks.ContainerSingleItem;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.teneted.neotenet.injection.world.level.block.entity.InjectionJukeboxBlockEntity;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Mixin(JukeboxBlockEntity.class)
public abstract class MixinJukeboxBlockEntity extends BlockEntity implements Clearable, ContainerSingleItem.BlockContainerSingleItem, InjectionJukeboxBlockEntity {

    @Shadow
    private ItemStack item;
    @Shadow
    public List<HumanEntity> transaction;
    @Shadow
    private int maxStack;

    @Shadow
    public abstract void setSongItemWithoutPlaying(ItemStack p_350615_);

    @Shadow
    @Final
    private JukeboxSongPlayer jukeboxSongPlayer;

    public MixinJukeboxBlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    @Override
    public List<ItemStack> getContents() {
        return Collections.singletonList(item);
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

    @ModifyReturnValue(method = "getMaxStackSize", at = @At("RETURN"))
    private int neotenet$useBukkitMaxStackSize(int original) {
        return maxStack;
    }

    private AtomicLong neotenet$ticksSinceSongStarted = new AtomicLong(0L);

    @Override
    public void setSongItemWithoutPlaying(ItemStack itemstack, long ticksSinceSongStarted) { // CraftBukkit - add argument
        neotenet$ticksSinceSongStarted.set(ticksSinceSongStarted);
        setSongItemWithoutPlaying(itemstack);
    }

    @Inject(method = "setSongItemWithoutPlaying", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/JukeboxSong;fromStack(Lnet/minecraft/core/HolderLookup$Provider;Lnet/minecraft/world/item/ItemStack;)Ljava/util/Optional;"))
    private void neotenet$resetSongs(ItemStack p_350615_, CallbackInfo ci) {
        this.jukeboxSongPlayer.song = null; // CraftBukkit - reset
    }

    @ModifyArg(method = "lambda$setSongItemWithoutPlaying$1", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/JukeboxSongPlayer;setSongWithoutPlaying(Lnet/minecraft/core/Holder;J)V"), index = 1)
    private long neotenet$setTicksSinceSongStarted(long p_350896_) {
        return neotenet$ticksSinceSongStarted.get();
    }

    @WrapWithCondition(method = "setSongItemWithoutPlaying", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;updateNeighborsAt(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;)V"))
    private boolean neotenet$checkLevel(Level instance, BlockPos blockPos, Block block) {
        return instance !=null;
    }
}