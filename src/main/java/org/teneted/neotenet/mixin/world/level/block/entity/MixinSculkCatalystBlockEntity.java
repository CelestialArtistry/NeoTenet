package org.teneted.neotenet.mixin.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SculkCatalystBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEventListener;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SculkCatalystBlockEntity.class)
public abstract class MixinSculkCatalystBlockEntity extends BlockEntity implements GameEventListener.Provider<SculkCatalystBlockEntity.CatalystListener> {

    @Shadow
    @Final
    private SculkCatalystBlockEntity.CatalystListener catalystListener;

    public MixinSculkCatalystBlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void neotenet$init(BlockPos p_222774_, BlockState p_222775_, CallbackInfo ci) {
        this.catalystListener.level = level; // CraftBukkit
    }

    @Inject(method = "serverTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/SculkSpreader;updateCursors(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/util/RandomSource;Z)V"))
    private static void neotenet$markSource(Level p_222780_, BlockPos p_222781_, BlockState p_222782_, SculkCatalystBlockEntity p_222783_, CallbackInfo ci) {
        org.bukkit.craftbukkit.event.CraftEventFactory.sourceBlockOverride = p_222783_.getBlockPos(); // CraftBukkit - SPIGOT-7068: Add source block override, not the most elegant way but better than passing down a BlockPosition up to five methods deep.
    }

    @Inject(method = "serverTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/SculkSpreader;updateCursors(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/util/RandomSource;Z)V", shift = At.Shift.AFTER))
    private static void neotenet$markSource0(Level p_222780_, BlockPos p_222781_, BlockState p_222782_, SculkCatalystBlockEntity p_222783_, CallbackInfo ci) {
        org.bukkit.craftbukkit.event.CraftEventFactory.sourceBlockOverride = null; // CraftBukkit
    }
}
