package org.teneted.neotenet.mixin.world.level.block.entity;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Arrays;

@Mixin(BrushableBlockEntity.class)
public abstract class MixinBrushableBlockEntity extends BlockEntity {

    public MixinBrushableBlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    @Redirect(method = "dropContent", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean neotenet$handleBlockDropItemEvent(Level instance, Entity entity, @Local(argsOnly = true) Player p_278006_) {
        // CraftBukkit start
        org.bukkit.block.Block bblock = CraftBlock.at(this.level, this.worldPosition);
        CraftEventFactory.handleBlockDropItemEvent(bblock, bblock.getState(), (ServerPlayer) p_278006_, Arrays.asList((ItemEntity) entity));
        // CraftBukkit end
        return true;
    }
}
