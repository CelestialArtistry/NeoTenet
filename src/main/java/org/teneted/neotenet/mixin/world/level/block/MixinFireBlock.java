package org.teneted.neotenet.mixin.world.level.block;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.block.CraftBlockState;
import org.bukkit.craftbukkit.block.CraftBlockStates;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.atomic.AtomicReference;

@Mixin(FireBlock.class)
public abstract class MixinFireBlock extends BaseFireBlock {

    @Shadow
    protected abstract BlockState getStateWithAge(LevelAccessor p_53438_, BlockPos p_53439_, int p_53440_);

    @Shadow
    protected abstract void checkBurnOut(Level par1, BlockPos par2, int par3, RandomSource par4, int par5, Direction par6);

    public MixinFireBlock(Properties p_49241_, float p_49242_) {
        super(p_49241_, p_49242_);
    }

    @ModifyReturnValue(method = "updateShape", at = @At("RETURN"))
    private BlockState neotenet$callBlockFadeEvent(BlockState original, @Local(argsOnly = true, ordinal = 0) BlockState p_53458_, @Local(argsOnly = true) Direction p_53459_, @Local(argsOnly = true, ordinal = 1) BlockState p_53460_, @Local(argsOnly = true) LevelAccessor p_53461_, @Local(ordinal = 0, argsOnly = true) BlockPos p_53462_, @Local(ordinal = 1, argsOnly = true) BlockPos p_53463_) {
        // CraftBukkit start
        if (!this.canSurvive(p_53458_, p_53461_, p_53462_)) {
            // Suppress during worldgen
            if (!(p_53461_ instanceof Level)) {
                return Blocks.AIR.defaultBlockState();
            }
            CraftBlockState blockState = CraftBlockStates.getBlockState(p_53461_, p_53462_);
            blockState.setData(Blocks.AIR.defaultBlockState());

            BlockFadeEvent event = new BlockFadeEvent(blockState.getBlock(), blockState);
            ((Level) p_53461_).getCraftServer().getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                return blockState.getHandle();
            }
        }
        return this.getStateWithAge(p_53461_, p_53462_, (Integer) p_53458_.getValue(FireBlock.AGE));
        // CraftBukkit end
    }

    @Redirect(method = {"tick", "checkBurnOut"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;removeBlock(Lnet/minecraft/core/BlockPos;Z)Z"))
    private boolean neotenet$useFireExtinguish(ServerLevel instance, BlockPos blockPos, boolean b) {
        fireExtinguished(instance, blockPos); // CraftBukkit
        return true;
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/FireBlock;checkBurnOut(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;ILnet/minecraft/util/RandomSource;ILnet/minecraft/core/Direction;)V"))
    private void neotenet$putSourcePos(BlockState p_221160_, ServerLevel p_221161_, BlockPos p_221162_, RandomSource p_221163_, CallbackInfo ci) {
        neotenet$sourcePos.set(p_221162_);
    }

    @Inject(method = "checkBurnOut", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextInt(I)I"), cancellable = true)
    private void neotenet$callBlockBurnEvent(Level p_221151_, BlockPos p_221152_, int p_221153_, RandomSource p_221154_, int p_221155_, Direction face, CallbackInfo ci) {
        // CraftBukkit start
        org.bukkit.block.Block theBlock = p_221151_.getWorld().getBlockAt(p_221152_.getX(), p_221152_.getY(), p_221152_.getZ());
        org.bukkit.block.Block sourceBlock = p_221151_.getWorld().getBlockAt(neotenet$sourcePos.get().getX(), neotenet$sourcePos.get().getY(), neotenet$sourcePos.get().getZ());

        BlockBurnEvent event = new BlockBurnEvent(theBlock, sourceBlock);
        p_221151_.getCraftServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            ci.cancel();
            return;
        }

        if (p_221151_.getBlockState(p_221152_).getBlock() instanceof TntBlock && !CraftEventFactory.callTNTPrimeEvent(p_221151_, p_221152_, org.bukkit.event.block.TNTPrimeEvent.PrimeCause.FIRE, null, neotenet$sourcePos.get())) {
            ci.cancel();
            return;
        }
        // CraftBukkit end
        neotenet$sourcePos.set(null);
    }

    private AtomicReference<BlockPos> neotenet$sourcePos = new AtomicReference<>();

    private void trySpread(Level p_221151_, BlockPos p_221152_, int p_221153_, RandomSource p_221154_, int p_221155_, Direction face, BlockPos sourceposition) {
        neotenet$sourcePos.set(sourceposition);
        checkBurnOut(p_221151_, p_221152_, p_221153_, p_221154_, p_221153_, face);
        neotenet$sourcePos.set(null);
    }

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", ordinal = 1))
    private boolean neotenet$callBukkitEvents(ServerLevel instance, BlockPos blockPos, BlockState state, int i, Operation<Boolean> original, @Local(argsOnly = true) BlockPos p_221162_) {
        // CraftBukkit start - Call to stop spread of fire
        if (instance.getBlockState(blockPos).getBlock() != Blocks.FIRE) {
            if (!CraftEventFactory.callBlockIgniteEvent(instance, blockPos, p_221162_).isCancelled()) {
                return false;
            }
        }
        // CraftBukkit end
        return  CraftEventFactory.handleBlockSpreadEvent(instance, p_221162_, blockPos, state, i); // CraftBukkit;
    }
}