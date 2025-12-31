package org.teneted.neotenet.mixin.world.level.block.state;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class MixinBlockStateBase {

    @Shadow
    public abstract Block getBlock();

    @Shadow
    protected abstract BlockState asState();

    public void onPlace(Level world, BlockPos blockposition, BlockState iblockdata, boolean flag, @Nullable UseOnContext context) {
        this.getBlock().onPlace0(this.asState(), world, blockposition, iblockdata, flag, context);
    }
}
