package org.teneted.neotenet.mixin.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(BannerBlockEntity.class)
public abstract class MixinBannerBlockEntity extends BlockEntity implements Nameable {

    @Shadow
    private BannerPatternLayers patterns;

    public MixinBannerBlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    @Redirect(method = "applyImplicitComponents", at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/block/entity/BannerBlockEntity;patterns:Lnet/minecraft/world/level/block/entity/BannerPatternLayers;", opcode = Opcodes.PUTFIELD))
    private void neotenet$applyLimits(BannerBlockEntity instance, BannerPatternLayers value) {
        this.setPatterns(value); // CraftBukkit - apply limits
    }

    @Redirect(method = "lambda$loadAdditional$1", at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/block/entity/BannerBlockEntity;patterns:Lnet/minecraft/world/level/block/entity/BannerPatternLayers;", opcode = Opcodes.PUTFIELD))
    private void neotenet$applyLimits0(BannerBlockEntity instance, BannerPatternLayers value) {
        this.setPatterns(value); // CraftBukkit - apply limits
    }

    // CraftBukkit start
    @Override
    public void setPatterns(BannerPatternLayers bannerpatternlayers) {
        if (bannerpatternlayers.layers().size() > 20) {
            bannerpatternlayers = new BannerPatternLayers(List.copyOf(bannerpatternlayers.layers().subList(0, 20)));
        }
        this.patterns = bannerpatternlayers;
    }
    // CraftBukkit end
}
