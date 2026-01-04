package org.teneted.neotenet.mixin.world.level.portal;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.PortalShape;
import org.bukkit.event.world.PortalCreateEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Mixin(PortalShape.class)
public class MixinPortalShape {

    @Shadow
    @Nullable
    private BlockPos bottomLeft;
    @Shadow
    @Final
    private LevelAccessor level;
    @Shadow
    @Final
    private Direction.Axis axis;
    @Shadow
    private int height;
    @Shadow
    @Final
    private Direction rightDir;
    @Shadow
    @Final
    private int width;
    org.bukkit.craftbukkit.util.BlockStateListPopulator blocks; // CraftBukkit - add field

    @Unique
    private AtomicBoolean createPortalBlocksBoolean =
            new AtomicBoolean(true);
    private AtomicReference<Entity> neotenet$entity = new AtomicReference<>();

    @Inject(method = "<init>", at = @At("RETURN"))
    private void neotenet$init(LevelAccessor p_77695_, BlockPos p_77696_, Direction.Axis p_77697_, CallbackInfo ci) {
        blocks = new org.bukkit.craftbukkit.util.BlockStateListPopulator(p_77695_.getMinecraftWorld()); // CraftBukkit
    }

    @Inject(method = "getDistanceUntilEdgeAboveFrame", at = @At(value = "RETURN", ordinal = 0))
    private void neotenet$setBlock0(BlockPos p_77736_, Direction p_77737_, CallbackInfoReturnable<Integer> cir, @Local BlockPos.MutableBlockPos blockpos$mutableblockpos, @Local BlockState blockstate) {
        blocks.setBlock(blockpos$mutableblockpos, blockstate, 18); // CraftBukkit - lower left / right
    }

    @Inject(method = "getDistanceUntilEdgeAboveFrame", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$StatePredicate;test(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Z", shift = At.Shift.AFTER))
    private void neotenet$setBlock1(BlockPos p_77736_, Direction p_77737_, CallbackInfoReturnable<Integer> cir,  @Local BlockPos.MutableBlockPos blockpos$mutableblockpos, @Local(ordinal = 0) BlockState blockstate) {
        blocks.setBlock(blockpos$mutableblockpos, blockstate, 18); // CraftBukkit - bottom row
    }

    @Inject(method = "hasTopFrame", at = @At("RETURN"))
    private void neotenet$setBlock2(BlockPos.MutableBlockPos p_77731_, int p_77732_, CallbackInfoReturnable<Boolean> cir, @Local(ordinal = 1) BlockPos.MutableBlockPos blockpos$mutableblockpos) {
        blocks.setBlock(blockpos$mutableblockpos, this.level.getBlockState(blockpos$mutableblockpos), 18); // CraftBukkit - upper row
    }

    @Inject(method = "getDistanceUntilTop", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void neotenet$setBlock3(BlockPos.MutableBlockPos p_77729_, CallbackInfoReturnable<Integer> cir, int i) {
        // CraftBukkit start - left and right
        blocks.setBlock(p_77729_.set(this.bottomLeft).move(Direction.UP, i).move(this.rightDir, -1), this.level.getBlockState(p_77729_), 18);
        blocks.setBlock(p_77729_.set(this.bottomLeft).move(Direction.UP, i).move(this.rightDir, this.width), this.level.getBlockState(p_77729_), 18);
        // CraftBukkit end
    }

    @Inject(method = "createPortalBlocks", at = @At("RETURN"), cancellable = true)
    private void neotenet$portalEvent(CallbackInfo ci, @Local BlockState blockstate) {
        org.bukkit.World bworld = this.level.getMinecraftWorld().getWorld();
        PortalCreateEvent event = new PortalCreateEvent((java.util.List<org.bukkit.block.BlockState>) (java.util.List) blocks.getList(), bworld, (neotenet$entity.get() == null) ? null : neotenet$entity.get().getBukkitEntity(), PortalCreateEvent.CreateReason.FIRE);
        this.level.getMinecraftWorld().getServer().server.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            createPortalBlocksBoolean.set(false);
            ci.cancel();
        }
        // CraftBukkit end
        BlockPos.betweenClosed(this.bottomLeft, this.bottomLeft.relative(Direction.UP, this.height - 1).relative(this.rightDir, this.width - 1)).forEach((blockposition) -> {
            this.level.setBlock(blockposition, blockstate, 18);
        });
        createPortalBlocksBoolean.set(true);
        neotenet$entity.getAndSet(null);
    }

    public boolean createPortalBlocks(Entity entity) {
        neotenet$entity.set(entity);
        org.bukkit.World bworld = this.level.getMinecraftWorld().getWorld();

        // Copy below for loop
        BlockState blockstate = Blocks.NETHER_PORTAL.defaultBlockState().setValue(NetherPortalBlock.AXIS, this.axis);
        BlockPos.betweenClosed(this.bottomLeft, this.bottomLeft.relative(Direction.UP, this.height - 1).relative(this.rightDir, this.width - 1)).forEach((blockposition) -> {
            blocks.setBlock(blockposition, blockstate, 18);
        });
        PortalCreateEvent event = new PortalCreateEvent((java.util.List<org.bukkit.block.BlockState>) (java.util.List) blocks.getList(), bworld, (entity == null) ? null : entity.getBukkitEntity(), PortalCreateEvent.CreateReason.FIRE);
        this.level.getMinecraftWorld().getServer().server.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            createPortalBlocksBoolean.set(false);
            return false;
        }
        // CraftBukkit end
        BlockPos.betweenClosed(this.bottomLeft, this.bottomLeft.relative(Direction.UP, this.height - 1).relative(this.rightDir, this.width - 1)).forEach((blockposition) -> {
            this.level.setBlock(blockposition, blockstate, 18);
        });
        createPortalBlocksBoolean.set(true);
        return true;
    }

}
