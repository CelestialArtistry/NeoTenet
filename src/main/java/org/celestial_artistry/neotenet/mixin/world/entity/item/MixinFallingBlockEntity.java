package org.celestial_artistry.neotenet.mixin.world.entity.item;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FallingBlockEntity.class)
public abstract class MixinFallingBlockEntity extends Entity {

    // @formatter:off
    @Shadow private BlockState blockState;
    // @formatter:on

    public MixinFallingBlockEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "fall(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/entity/item/FallingBlockEntity;", cancellable = true, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private static void neotenet$entityFall(Level level, BlockPos pos, BlockState state, CallbackInfoReturnable<FallingBlockEntity> cir, @Local FallingBlockEntity entity) {
        if (!CraftEventFactory.callEntityChangeBlockEvent(entity, pos, state.getFluidState().createLegacyBlock())) {
            cir.setReturnValue(entity);
        }
    }

    @Inject(method = "tick", cancellable = true, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private void neotenet$entityChangeBlock(CallbackInfo ci, @Local BlockPos pos) {
        if (!CraftEventFactory.callEntityChangeBlockEvent((FallingBlockEntity) (Object) this, pos, this.blockState)) {
            ci.cancel();
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void neotenet$addData(CompoundTag compoundTag, CallbackInfo ci) {
        // Paper start - Try and load origin location from the old NBT tags for backwards compatibility
        if (compoundTag.contains("SourceLoc_x")) {
            int srcX = compoundTag.getInt("SourceLoc_x");
            int srcY = compoundTag.getInt("SourceLoc_y");
            int srcZ = compoundTag.getInt("SourceLoc_z");
            this.setOrigin(new org.bukkit.Location(this.level().getWorld(), srcX, srcY, srcZ));
        }
        // Paper end
    }
}
