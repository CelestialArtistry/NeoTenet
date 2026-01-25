package org.teneted.neotenet.mixin.world.level.block;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BasePressurePlateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(BasePressurePlateBlock.class)
public class MixinBasePressurePlateBlock {

    @Definition(id = "p_152148_", local = @Local(type = int.class, ordinal = 0, argsOnly = true))
    @Definition(id = "i", local = @Local(type = int.class, ordinal = 1))
    @Expression("p_152148_ != i")
    @Inject(method = "checkPressed", at = @At("MIXINEXTRAS:EXPRESSION"))
    private void neotenet$callBlockRedstoneEvent(Entity p_152144_, Level p_152145_, BlockPos p_152146_, BlockState p_152147_, int p_152148_, CallbackInfo ci, @Local(ordinal = 1) int j, @Local(ordinal = 0) boolean flag, @Local(ordinal = 1) boolean flag1) {
        // CraftBukkit start - Interact Pressure Plate
        org.bukkit.World bworld = p_152145_.getWorld();
        org.bukkit.plugin.PluginManager manager = p_152145_.getCraftServer().getPluginManager();

        if (flag != flag1) {
            BlockRedstoneEvent eventRedstone = new BlockRedstoneEvent(bworld.getBlockAt(p_152146_.getX(), p_152146_.getY(), p_152146_.getZ()), p_152148_, j);
            manager.callEvent(eventRedstone);

            flag1 = eventRedstone.getNewCurrent() > 0;
            j = eventRedstone.getNewCurrent();
        }
        // CraftBukkit end
    }

    @SuppressWarnings("unchecked")
    protected static <T extends Entity> List<T> getEntities(Level p_289656_, AABB p_289647_, Class<? extends Entity> p_289686_) {
        return (List<T>) p_289656_.getEntitiesOfClass(p_289686_, p_289647_, EntitySelector.NO_SPECTATORS.and(p_289691_ -> !p_289691_.isIgnoringBlockTriggers()));
    }
}
