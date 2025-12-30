package org.teneted.neotenet.mixin.world.level.block;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DoorBlock.class)
public abstract class MixinDoorBlock extends Block {

    public MixinDoorBlock(Properties properties) {
        super(properties);
    }

    @ModifyExpressionValue(method = "neighborChanged", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z"))
    private boolean neotenet$resetFalg(boolean original, @Local(argsOnly = true) BlockState p_52776_,
                                       @Local(argsOnly = true) Level p_52777_,
                                       @Local(ordinal = 0, argsOnly = true) BlockPos p_52778, @Local(ordinal = 1) boolean flag) {
        BlockPos otherHalf = p_52778.relative(p_52776_.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER ? Direction.UP : Direction.DOWN);
        org.bukkit.World bworld = p_52777_.getWorld();
        org.bukkit.block.Block bukkitBlock = bworld.getBlockAt(p_52778.getX(), p_52778.getY(), p_52778.getZ());
        org.bukkit.block.Block blockTop = bworld.getBlockAt(otherHalf.getX(), otherHalf.getY(), otherHalf.getZ());

        int power = bukkitBlock.getBlockPower();
        int powerTop = blockTop.getBlockPower();
        if (powerTop > power) power = powerTop;
        int oldPower = (Boolean) p_52776_.getValue(DoorBlock.POWERED) ? 15 : 0;

        if (oldPower == 0 ^ power == 0) {
            BlockRedstoneEvent eventRedstone = new BlockRedstoneEvent(bukkitBlock, oldPower, power);
            p_52777_.getCraftServer().getPluginManager().callEvent(eventRedstone);

            flag = eventRedstone.getNewCurrent() > 0;
            // CraftBukkit end
        }
        return oldPower == 0 ^ power == 0;
    }
}
