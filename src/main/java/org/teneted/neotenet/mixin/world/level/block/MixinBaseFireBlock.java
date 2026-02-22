package org.teneted.neotenet.mixin.world.level.block;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.teneted.neotenet.injection.world.level.block.InjectionBaseFireBlock;

@Mixin(BaseFireBlock.class)
public class MixinBaseFireBlock implements InjectionBaseFireBlock {

    @Redirect(method = "entityInside", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;igniteForSeconds(F)V"))
    private void neotenet$callEntityCombustByBlockEvent(Entity instance, float v,
                                                        @Local(argsOnly = true) Level p_49261_,
                                                        @Local(argsOnly = true) BlockPos p_49262_) {
        // CraftBukkit start
        org.bukkit.event.entity.EntityCombustEvent event = new org.bukkit.event.entity.EntityCombustByBlockEvent(org.bukkit.craftbukkit.block.CraftBlock.at(p_49261_, p_49262_), instance.getBukkitEntity(), 8.0F);
        p_49261_.getCraftServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            instance.igniteForSeconds(event.getDuration(), false);
        }
        // CraftBukkit end
    }

    // CraftBukkit start
    @Override
    public void fireExtinguished(net.minecraft.world.level.LevelAccessor world, BlockPos position) {
        if (!org.bukkit.craftbukkit.event.CraftEventFactory.callBlockFadeEvent(world, position, Blocks.AIR.defaultBlockState()).isCancelled()) {
            world.removeBlock(position, false);
        }
    }
    // CraftBukkit end

    @ModifyReturnValue(method = "inPortalDimension", at = @At("RETURN"))
    private static boolean neotenet$useLevelStem(boolean original, @Local(argsOnly = true) Level world) {
        return world.getTypeKey() == net.minecraft.world.level.dimension.LevelStem.OVERWORLD || world.getTypeKey() == net.minecraft.world.level.dimension.LevelStem.NETHER; // CraftBukkit - getTypeKey()
    }
}
