package org.teneted.neotenet.mixin.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SpongeBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.bukkit.craftbukkit.block.CraftBlockState;
import org.bukkit.craftbukkit.util.BlockStateListPopulator;
import org.bukkit.event.block.SpongeAbsorbEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(SpongeBlock.class)
public abstract class MixinSpongeBlock extends Block {

    @Shadow
    @Final
    private static Direction[] ALL_DIRECTIONS;

    public MixinSpongeBlock(Properties p_49795_) {
        super(p_49795_);
    }

    /**
     * @author wdog5734
     * @reason bukkit
     */
    @Overwrite
    private boolean removeWaterBreadthFirstSearch(Level p_56808_, BlockPos p_56809_) {
        BlockState spongeState = p_56808_.getBlockState(p_56809_);
        BlockStateListPopulator blockList = new BlockStateListPopulator(p_56808_); // CraftBukkit - Use BlockStateListPopulator
        BlockPos.breadthFirstTraversal(
                p_56809_,
                6,
                65,
                (p_277519_, p_277492_) -> {
                    for (Direction direction : ALL_DIRECTIONS) {
                        p_277492_.accept(p_277519_.relative(direction));
                    }
                },
                p_294069_ -> {
                    if (p_294069_.equals(p_56809_)) {
                        return true;
                    } else {
                        // CraftBukkit start
                        BlockState blockstate = blockList.getBlockState(p_294069_);
                        FluidState fluidstate = blockList.getFluidState(p_294069_);
                        // CraftBukkit end
                        if (!spongeState.canBeHydrated(p_56808_, p_56809_, fluidstate, p_294069_)) {
                            return false;
                        } else {
                            if (blockstate.getBlock() instanceof BucketPickup bucketpickup
                                    && !bucketpickup.pickupBlock(null, blockList, p_294069_, blockstate).isEmpty()) {
                                return true;
                            }

                            if (blockstate.getBlock() instanceof LiquidBlock) {
                                blockList.setBlock(p_294069_, Blocks.AIR.defaultBlockState(), 3);
                            } else {
                                if (!blockstate.is(Blocks.KELP)
                                        && !blockstate.is(Blocks.KELP_PLANT)
                                        && !blockstate.is(Blocks.SEAGRASS)
                                        && !blockstate.is(Blocks.TALL_SEAGRASS)) {
                                    return false;
                                }

                                // CraftBukkit start
                                // TileEntity tileentity = iblockdata.hasBlockEntity() ? world.getBlockEntity(blockposition1) : null;
                                p_56808_.setBlock(p_294069_, Blocks.AIR.defaultBlockState(), 3);
                                // dropResources(iblockdata, world, blockposition1, tileentity);
                                blockList.setBlock(p_294069_, Blocks.AIR.defaultBlockState(), 3);
                                // CraftBukkit end
                            }

                            return true;
                        }
                    }
                }
        );
        // CraftBukkit start
        List<CraftBlockState> blocks = blockList.getList(); // Is a clone
        if (!blocks.isEmpty()) {
            final org.bukkit.block.Block bblock = p_56808_.getWorld().getBlockAt(p_56809_.getX(), p_56809_.getY(), p_56809_.getZ());

            SpongeAbsorbEvent event = new SpongeAbsorbEvent(bblock, (List<org.bukkit.block.BlockState>) (List) blocks);
            p_56808_.getCraftServer().getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                return false;
            }

            for (CraftBlockState block : blocks) {
                BlockPos blockposition1 = block.getPosition();
                BlockState iblockdata = p_56808_.getBlockState(blockposition1);
                FluidState fluid = p_56808_.getFluidState(blockposition1);

                if (fluid.is(FluidTags.WATER)) {
                    if (iblockdata.getBlock() instanceof BucketPickup && !((BucketPickup) iblockdata.getBlock()).pickupBlock((Player) null, blockList, blockposition1, iblockdata).isEmpty()) {
                        // NOP
                    } else if (iblockdata.getBlock() instanceof LiquidBlock) {
                        // NOP
                    } else if (iblockdata.is(Blocks.KELP) || iblockdata.is(Blocks.KELP_PLANT) || iblockdata.is(Blocks.SEAGRASS) || iblockdata.is(Blocks.TALL_SEAGRASS)) {
                        BlockEntity tileentity = iblockdata.hasBlockEntity() ? p_56808_.getBlockEntity(blockposition1) : null;

                        dropResources(iblockdata, p_56808_, blockposition1, tileentity);
                    }
                }
                p_56808_.setBlock(blockposition1, block.getHandle(), block.getFlag());
            }

            return true;
        }
        return false;
        // CraftBukkit end
    }
}
