package org.teneted.neotenet.mixin.world.level.block;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BambooStalkBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BambooLeaves;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BambooStalkBlock.class)
public abstract class MixinBambooStalkBlock extends Block {

    public MixinBambooStalkBlock(Properties p_49795_) {
        super(p_49795_);
    }

    @ModifyExpressionValue(method = "performBonemeal", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;isEmptyBlock(Lnet/minecraft/core/BlockPos;)Z"))
    private boolean neotenet$checkBamboo(boolean original, @Local(ordinal = 0) BlockState blockstate) {
        return original || !blockstate.is(Blocks.BAMBOO);
    }

    @Inject(method = "growBamboo", at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/block/state/properties/BambooLeaves;NONE:Lnet/minecraft/world/level/block/state/properties/BambooLeaves;", shift = At.Shift.AFTER, opcode = Opcodes.GETSTATIC))
    private void neotenet$addField(BlockState p_261855_, Level p_262076_, BlockPos p_262109_, RandomSource p_261633_, int p_261759_, CallbackInfo ci, @Share("shouldUpdateOthers") LocalBooleanRef shouldUpdateOthers) {
        shouldUpdateOthers.set(false);
    }

    @Redirect(method = "growBamboo", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private boolean neotenet$cancelSetBLock(Level instance, BlockPos p_46601_, BlockState p_46602_, int p_46603_, @Share("shouldUpdateOthers") LocalBooleanRef shouldUpdateOthers) {
        shouldUpdateOthers.set(true);
        return false;
    }

    @Inject(method = "growBamboo", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", ordinal = 2))
    private void neotenet$callEvent(BlockState p_261855_, Level p_262076_, BlockPos p_262109_, RandomSource p_261633_, int p_261759_, CallbackInfo ci, @Share("shouldUpdateOthers") LocalBooleanRef shouldUpdateOthers,
                                    @Local(ordinal = 1) int j, @Local(ordinal = 2) int k,
                                    @Local BambooLeaves bambooleaves,
                                    @Local(ordinal = 1) BlockPos blockpos,
                                    @Local(ordinal = 1) BlockState blockstate,
                                    @Local(ordinal = 2) BlockState blockstate1) {
        // CraftBukkit start
        if (CraftEventFactory.handleBlockSpreadEvent(p_262076_, p_262109_, p_262109_.above(), (BlockState) ((BlockState) ((BlockState) this.defaultBlockState().setValue(BambooStalkBlock.AGE, j)).setValue(BambooStalkBlock.LEAVES, bambooleaves)).setValue(BambooStalkBlock.STAGE, k), 3)) {
            if (shouldUpdateOthers.get()) {
                p_262076_.setBlock(p_262109_.below(), (BlockState) blockstate.setValue(BambooStalkBlock.LEAVES, BambooLeaves.SMALL), 3);
                p_262076_.setBlock(blockpos, (BlockState) blockstate1.setValue(BambooStalkBlock.LEAVES, BambooLeaves.NONE), 3);
            }
        }
        // CraftBukkit end
    }
}

