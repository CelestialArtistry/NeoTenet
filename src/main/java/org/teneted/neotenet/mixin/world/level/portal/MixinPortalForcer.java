package org.teneted.neotenet.mixin.world.level.portal;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.portal.PortalForcer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.teneted.neotenet.injection.world.level.portal.InjectionPortalForcer;

import java.util.Comparator;
import java.util.Optional;

@Mixin(PortalForcer.class)
public abstract class MixinPortalForcer implements InjectionPortalForcer {

    @Shadow
    @Final
    private ServerLevel level;

    @Shadow
    protected abstract boolean canHostFrame(BlockPos p_77662_, BlockPos.MutableBlockPos p_77663_, Direction p_77664_, int p_77665_);

    @Shadow
    protected abstract boolean canPortalReplaceBlock(BlockPos.MutableBlockPos p_248971_);

    @Inject(method = "createPortal", at = @At("HEAD"))
    private void neotenet$setBlockList(BlockPos p_77667_, Direction.Axis p_77668_, CallbackInfoReturnable<Optional<BlockUtil.FoundRectangle>> cir,
                                       @Share("bukkitBlockList") LocalRef<org.bukkit.craftbukkit.util.BlockStateListPopulator> bukkitBlockList) {
        org.bukkit.craftbukkit.util.BlockStateListPopulator blockList = new org.bukkit.craftbukkit.util.BlockStateListPopulator(this.level); // CraftBukkit - Use BlockStateListPopulator
        bukkitBlockList.set(blockList);
    }

    @Redirect(method = "createPortal", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private boolean neotenet$resetBlockListSetBlock(ServerLevel instance, BlockPos blockPos, BlockState state, int i, @Share("bukkitBlockList") LocalRef<org.bukkit.craftbukkit.util.BlockStateListPopulator> bukkitBlockList) {
        return bukkitBlockList.get().setBlock(blockPos, state, i); // CraftBukkit
    }

    @Redirect(method = "createPortal", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private boolean neotenet$useBlockListSetBlockAndUpdate(ServerLevel instance, BlockPos blockPos, BlockState state, @Share("bukkitBlockList") LocalRef<org.bukkit.craftbukkit.util.BlockStateListPopulator> bukkitBlockList) {
        return bukkitBlockList.get().setBlock(blockPos, state, 3);
    }

    @Inject(method = "createPortal", at = @At("RETURN"), cancellable = true)
    private void neotenet$callPortalCreateEvent(BlockPos p_77667_, Direction.Axis p_77668_, CallbackInfoReturnable<Optional<BlockUtil.FoundRectangle>> cir, @Share("bukkitBlockList") LocalRef<org.bukkit.craftbukkit.util.BlockStateListPopulator> bukkitBlockList) {
        // CraftBukkit start
        org.bukkit.World bworld = this.level.getWorld();
        org.bukkit.event.world.PortalCreateEvent event = new org.bukkit.event.world.PortalCreateEvent((java.util.List<org.bukkit.block.BlockState>) (java.util.List) bukkitBlockList.get().getList(), bworld, null, org.bukkit.event.world.PortalCreateEvent.CreateReason.NETHER_PAIR);

        this.level.getCraftServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            cir.setReturnValue(Optional.empty());
        }
        bukkitBlockList.get().updateList();
        // CraftBukkit end
    }

    @Override
    public Optional<BlockPos> findClosestPortalPosition(BlockPos p_352378_, boolean p_352309_, WorldBorder p_352374_, int i) {
        PoiManager poimanager = this.level.getPoiManager();
        poimanager.ensureLoadedAndValid(this.level, p_352378_, i);
        return poimanager.getInSquare(p_230634_ -> p_230634_.is(PoiTypes.NETHER_PORTAL), p_352378_, i, PoiManager.Occupancy.ANY)
                .map(PoiRecord::getPos)
                .filter(p_352374_::isWithinBounds)
                .filter(p_352047_ -> this.level.getBlockState(p_352047_).hasProperty(BlockStateProperties.HORIZONTAL_AXIS))
                .min(Comparator.<BlockPos>comparingDouble(p_352046_ -> p_352046_.distSqr(p_352378_)).thenComparingInt(Vec3i::getY));
    }

    @Override
    public Optional<BlockUtil.FoundRectangle> createPortal(BlockPos p_77667_, Direction.Axis p_77668_, net.minecraft.world.entity.Entity entity, int createRadius) {
        Direction direction = Direction.get(Direction.AxisDirection.POSITIVE, p_77668_);
        double d0 = -1.0;
        BlockPos blockpos = null;
        double d1 = -1.0;
        BlockPos blockpos1 = null;
        WorldBorder worldborder = this.level.getWorldBorder();
        int i = Math.min(this.level.getMaxBuildHeight(), this.level.getMinBuildHeight() + this.level.getLogicalHeight()) - 1;
        int j = 1;
        BlockPos.MutableBlockPos blockpos$mutableblockpos = p_77667_.mutable();

        for (BlockPos.MutableBlockPos blockpos$mutableblockpos1 : BlockPos.spiralAround(p_77667_, createRadius, Direction.EAST, Direction.SOUTH)) {
            int k = Math.min(i, this.level.getHeight(Heightmap.Types.MOTION_BLOCKING, blockpos$mutableblockpos1.getX(), blockpos$mutableblockpos1.getZ()));
            if (worldborder.isWithinBounds(blockpos$mutableblockpos1) && worldborder.isWithinBounds(blockpos$mutableblockpos1.move(direction, 1))) {
                blockpos$mutableblockpos1.move(direction.getOpposite(), 1);

                for (int l = k; l >= this.level.getMinBuildHeight(); l--) {
                    blockpos$mutableblockpos1.setY(l);
                    if (this.canPortalReplaceBlock(blockpos$mutableblockpos1)) {
                        int i1 = l;

                        while (l > this.level.getMinBuildHeight() && this.canPortalReplaceBlock(blockpos$mutableblockpos1.move(Direction.DOWN))) {
                            l--;
                        }

                        if (l + 4 <= i) {
                            int j1 = i1 - l;
                            if (j1 <= 0 || j1 >= 3) {
                                blockpos$mutableblockpos1.setY(l);
                                if (this.canHostFrame(blockpos$mutableblockpos1, blockpos$mutableblockpos, direction, 0)) {
                                    double d2 = p_77667_.distSqr(blockpos$mutableblockpos1);
                                    if (this.canHostFrame(blockpos$mutableblockpos1, blockpos$mutableblockpos, direction, -1)
                                            && this.canHostFrame(blockpos$mutableblockpos1, blockpos$mutableblockpos, direction, 1)
                                            && (d0 == -1.0 || d0 > d2)) {
                                        d0 = d2;
                                        blockpos = blockpos$mutableblockpos1.immutable();
                                    }

                                    if (d0 == -1.0 && (d1 == -1.0 || d1 > d2)) {
                                        d1 = d2;
                                        blockpos1 = blockpos$mutableblockpos1.immutable();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (d0 == -1.0 && d1 != -1.0) {
            blockpos = blockpos1;
            d0 = d1;
        }

        org.bukkit.craftbukkit.util.BlockStateListPopulator blockList = new org.bukkit.craftbukkit.util.BlockStateListPopulator(this.level); // CraftBukkit - Use BlockStateListPopulator
        if (d0 == -1.0) {
            int k1 = Math.max(this.level.getMinBuildHeight() - -1, 70);
            int i2 = i - 9;
            if (i2 < k1) {
                return Optional.empty();
            }

            blockpos = new BlockPos(p_77667_.getX() - direction.getStepX() * 1, Mth.clamp(p_77667_.getY(), k1, i2), p_77667_.getZ() - direction.getStepZ() * 1)
                    .immutable();
            blockpos = worldborder.clampToBounds(blockpos);
            Direction direction1 = direction.getClockWise();

            for (int i3 = -1; i3 < 2; i3++) {
                for (int j3 = 0; j3 < 2; j3++) {
                    for (int k3 = -1; k3 < 3; k3++) {
                        BlockState blockstate1 = k3 < 0 ? Blocks.OBSIDIAN.defaultBlockState() : Blocks.AIR.defaultBlockState();
                        blockpos$mutableblockpos.setWithOffset(
                                blockpos, j3 * direction.getStepX() + i3 * direction1.getStepX(), k3, j3 * direction.getStepZ() + i3 * direction1.getStepZ()
                        );
                        blockList.setBlock(blockpos$mutableblockpos, blockstate1, 3); // CraftBukkit
                    }
                }
            }
        }

        for (int l1 = -1; l1 < 3; l1++) {
            for (int j2 = -1; j2 < 4; j2++) {
                if (l1 == -1 || l1 == 2 || j2 == -1 || j2 == 3) {
                    blockpos$mutableblockpos.setWithOffset(blockpos, l1 * direction.getStepX(), j2, l1 * direction.getStepZ());
                    blockList.setBlock(blockpos$mutableblockpos, Blocks.OBSIDIAN.defaultBlockState(), 3); // CraftBukkit
                }
            }
        }

        BlockState blockstate = Blocks.NETHER_PORTAL.defaultBlockState().setValue(NetherPortalBlock.AXIS, p_77668_);

        for (int k2 = 0; k2 < 2; k2++) {
            for (int l2 = 0; l2 < 3; l2++) {
                blockpos$mutableblockpos.setWithOffset(blockpos, k2 * direction.getStepX(), l2, k2 * direction.getStepZ());
                blockList.setBlock(blockpos$mutableblockpos, blockstate, 18); // CraftBukkit
            }
        }

        // CraftBukkit start
        org.bukkit.World bworld = this.level.getWorld();
        org.bukkit.event.world.PortalCreateEvent event = new org.bukkit.event.world.PortalCreateEvent((java.util.List<org.bukkit.block.BlockState>) (java.util.List) blockList.getList(), bworld, (entity == null) ? null : entity.getBukkitEntity(), org.bukkit.event.world.PortalCreateEvent.CreateReason.NETHER_PAIR);

        this.level.getCraftServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return Optional.empty();
        }
        blockList.updateList();
        // CraftBukkit end
        return Optional.of(new BlockUtil.FoundRectangle(blockpos.immutable(), 2, 3));
    }
}
