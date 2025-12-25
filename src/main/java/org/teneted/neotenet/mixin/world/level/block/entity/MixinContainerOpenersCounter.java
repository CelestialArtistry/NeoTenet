package org.teneted.neotenet.mixin.world.level.block.entity;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.teneted.neotenet.injection.world.level.block.entity.InjectionContainerOpenersCounter;

@Mixin(ContainerOpenersCounter.class)
public abstract class MixinContainerOpenersCounter implements InjectionContainerOpenersCounter {

    @Shadow
    protected abstract void onOpen(Level p_155460_, BlockPos p_155461_, net.minecraft.world.level.block.state.BlockState p_155462_);

    @Shadow
    protected abstract void onClose(Level p_155473_, BlockPos p_155474_, net.minecraft.world.level.block.state.BlockState p_155475_);

    @Shadow
    protected abstract void openerCountChanged(Level p_155463_, BlockPos p_155464_, net.minecraft.world.level.block.state.BlockState p_155465_, int p_155466_, int p_155467_);

    @Shadow
    private int openCount;

    @Shadow
    public boolean opened;
    @Unique
    int oldPower = Math.max(0, Math.min(15, this.openCount)); // CraftBukkit - Get power before new viewer is added

    @Inject(method = "incrementOpeners", at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/block/entity/ContainerOpenersCounter;openCount:I", ordinal = 0, shift = At.Shift.AFTER))
    private void neotenet$callRedstoneChange(Player p_155453_, Level p_155454_, BlockPos p_155455_, BlockState p_155456_, CallbackInfo ci) {
        // CraftBukkit start - Call redstone event
        if (p_155454_.getBlockState(p_155455_).is(net.minecraft.world.level.block.Blocks.TRAPPED_CHEST)) {
            int newPower = Math.max(0, Math.min(15, this.openCount));

            if (oldPower != newPower) {
                org.bukkit.craftbukkit.event.CraftEventFactory.callRedstoneChange(p_155454_, p_155455_, oldPower, newPower);
            }
        }
        // CraftBukkit end
    }

    @Inject(method = "decrementOpeners",
            at = @At(value = "FIELD",
            target = "Lnet/minecraft/world/level/block/entity/ContainerOpenersCounter;openCount:I",
            ordinal = 0, shift = At.Shift.AFTER))
    private void neotenet$callRedstoneChange0(Player p_155469_, Level p_155470_, BlockPos p_155471_, BlockState p_155472_, CallbackInfo ci) {
        // CraftBukkit start - Call redstone event
        if (p_155470_.getBlockState(p_155471_).is(net.minecraft.world.level.block.Blocks.TRAPPED_CHEST)) {
            int newPower = Math.max(0, Math.min(15, this.openCount));

            if (oldPower != newPower) {
                org.bukkit.craftbukkit.event.CraftEventFactory.callRedstoneChange(p_155470_, p_155471_, oldPower, newPower);
            }
        }
        // CraftBukkit end
    }

    @Inject(method = "recheckOpeners", at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/block/entity/ContainerOpenersCounter;openCount:I", ordinal = 0, shift = At.Shift.AFTER))
    private void neotenet$DummyCount(Level p_155477_, BlockPos p_155478_, BlockState p_155479_, CallbackInfo ci, @Local int i) {
        if (opened) i++; // CraftBukkit - add dummy count from API
    }

    // CraftBukkit start
    @Override
    public void onAPIOpen(Level world, BlockPos blockposition, BlockState iblockdata) {
        onOpen(world, blockposition, iblockdata);
    }

    @Override
    public void onAPIClose(Level world, BlockPos blockposition, BlockState iblockdata) {
        onClose(world, blockposition, iblockdata);
    }

    @Override
    public void openerAPICountChanged(Level world, BlockPos blockposition, BlockState iblockdata, int i, int j) {
        openerCountChanged(world, blockposition, iblockdata, i, j);
    }
    // CraftBukkit end
}
