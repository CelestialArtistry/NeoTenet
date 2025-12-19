package org.taiyitistmc.mixin.world.level.block;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.taiyitistmc.injection.world.level.block.InjectionChestBlock;

@Mixin(ChestBlock.class)
public abstract class MixinChestBlock implements InjectionChestBlock {

    @Shadow
    @Final
    private static DoubleBlockCombiner.Combiner<ChestBlockEntity, Optional<MenuProvider>> MENU_PROVIDER_COMBINER;

    @Shadow
    public abstract DoubleBlockCombiner.NeighborCombineResult<? extends ChestBlockEntity> combine(BlockState state, Level level, BlockPos pos, boolean override);

    @Override
    public MenuProvider getMenuProvider(BlockState iblockdata, Level world, BlockPos blockposition, boolean ignoreObstructions) {
        return this.combine(iblockdata, world, blockposition, ignoreObstructions).apply(MENU_PROVIDER_COMBINER).orElse(null);
    }
}
