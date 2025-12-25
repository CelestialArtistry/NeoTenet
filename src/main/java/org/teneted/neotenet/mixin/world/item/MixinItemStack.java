package org.teneted.neotenet.mixin.world.item;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BedItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.item.SolidBucketItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WitherSkullBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.TreeType;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.block.CapturedBlockState;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.block.CraftBlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.event.block.BlockFertilizeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerSignOpenEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.teneted.neotenet.injection.world.item.InjectionItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

@Mixin(ItemStack.class)
public abstract class MixinItemStack  implements DataComponentHolder, net.neoforged.neoforge.common.MutableDataComponentHolder, net.neoforged.neoforge.common.extensions.IItemStackExtension, InjectionItemStack {

    @Shadow
    @Final
    private PatchedDataComponentMap components;

    @Shadow
    public abstract int getCount();

    @Shadow
    public abstract void setCount(int p_41765_);

    @Shadow
    public abstract Item getItem();

    @Mutable
    @Shadow
    @Final
    @Deprecated
    @Nullable
    private Item item;

    @Shadow
    private int count;
    @Unique
    InteractionResult enuminteractionresult;
    @Unique
    int oldCount = this.getCount();
    @Unique
    DataComponentPatch newData = this.components.asPatch();
    @Unique
    int newCount = this.getCount();
    @Unique
    DataComponentPatch oldData = this.components.asPatch();

    @Inject(method = "onItemUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/InteractionResult;indicateItemUse()Z"))
    private void neotenet$bukkitHandleItem(UseOnContext p_41662_, Function<UseOnContext, InteractionResult> callback, CallbackInfoReturnable<InteractionResult> cir, @Local(ordinal = 0) Player player, @Local(ordinal = 0) BlockPos blockpos, @Local(ordinal = 0) Item item) {
        // CraftBukkit start - handle all block place event logic here
        ServerLevel world = (ServerLevel) p_41662_.getLevel();

        if (!(item instanceof BucketItem || item instanceof SolidBucketItem)) { // if not bucket
            world.captureBlockStates = true;
            // special case bonemeal
            if (item == Items.BONE_MEAL) {
                world.captureTreeGeneration = true;
            }
        }
        try {
            enuminteractionresult = item.useOn(p_41662_);
        } finally {
            world.captureBlockStates = false;
        }
        this.setCount(oldCount);
        this.restorePatch(oldData);
        if (enuminteractionresult.consumesAction() && world.captureTreeGeneration && world.capturedBlockStates.size() > 0) {
            world.captureTreeGeneration = false;
            Location location = CraftLocation.toBukkit(blockpos, world.getWorld());
            TreeType treeType = SaplingBlock.treeType;
            SaplingBlock.treeType = null;
            List<CraftBlockState> blocks = new ArrayList<>(world.capturedBlockStates.values());
            world.capturedBlockStates.clear();
            StructureGrowEvent structureEvent = null;
            if (treeType != null) {
                boolean isBonemeal = getItem() instanceof BoneMealItem;
                structureEvent = new StructureGrowEvent(location, treeType, isBonemeal, (org.bukkit.entity.Player) player.getBukkitEntity(), (List<BlockState>) (List<? extends BlockState>) blocks);
                Bukkit.getPluginManager().callEvent(structureEvent);
            }

            BlockFertilizeEvent fertilizeEvent = new BlockFertilizeEvent(CraftBlock.at(world, blockpos), (org.bukkit.entity.Player) player.getBukkitEntity(), (List< BlockState>) (List<? extends BlockState>) blocks);
            fertilizeEvent.setCancelled(structureEvent != null && structureEvent.isCancelled());
            Bukkit.getPluginManager().callEvent(fertilizeEvent);

            if (!fertilizeEvent.isCancelled()) {
                // Change the stack to its new contents if it hasn't been tampered with.
                if (this.getCount() == oldCount && Objects.equals(this.components.asPatch(), oldData)) {
                    this.restorePatch(newData);
                    this.setCount(newCount);
                }
                for (CraftBlockState blockstate : blocks) {
                    // SPIGOT-7572 - Move fix for SPIGOT-7248 to CapturedBlockState, to allow bees in bee nest
                    CapturedBlockState.setBlockState(blockstate);
                }
                player.awardStat(Stats.ITEM_USED.get(item)); // SPIGOT-7236 - award stat
            }

            SignItem.openSign = null; // SPIGOT-6758 - Reset on early return
            cir.setReturnValue(enuminteractionresult);
        }
        world.captureTreeGeneration = false;
    }

    @Redirect(method = "onItemUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;awardStat(Lnet/minecraft/stats/Stat;)V"))
    private void neotenet$bukkitAwardStat(Player instance, Stat<?> p_36247_, @Local(argsOnly = true) UseOnContext p_41662_, @Local(ordinal = 0) BlockPos blockpos, @Local(ordinal = 0) Item item) {
        InteractionHand enumhand = p_41662_.getHand();
        BlockPlaceEvent placeEvent = null;
        var level = p_41662_.getLevel();
        if (!(level instanceof ServerLevel world)) {
            return;
        }
        List<BlockState> blocks = new ArrayList<>(world.capturedBlockStates.values());
        world.capturedBlockStates.clear();
        if (blocks.size() > 1) {
            placeEvent = CraftEventFactory.callBlockMultiPlaceEvent(world, instance, enumhand, blocks, blockpos.getX(), blockpos.getY(), blockpos.getZ());
        } else if (blocks.size() == 1) {
            placeEvent = CraftEventFactory.callBlockPlaceEvent(world, instance, enumhand, blocks.get(0), blockpos.getX(), blockpos.getY(), blockpos.getZ());
        }

        if (placeEvent != null && (placeEvent.isCancelled() || !placeEvent.canBuild())) {
            enuminteractionresult = InteractionResult.FAIL; // cancel placement
            // PAIL: Remove this when MC-99075 fixed
            placeEvent.getPlayer().updateInventory();
            // revert back all captured blocks
            world.preventPoiUpdated = true; // CraftBukkit - SPIGOT-5710
            for (BlockState blockstate : blocks) {
                blockstate.update(true, false);
            }
            world.preventPoiUpdated = false;

            // Brute force all possible updates
            BlockPos placedPos = ((CraftBlock) placeEvent.getBlock()).getPosition();
            for (Direction dir : Direction.values()) {
                ((ServerPlayer) instance).connection.send(new ClientboundBlockUpdatePacket(world, placedPos.relative(dir)));
            }
            SignItem.openSign = null; // SPIGOT-6758 - Reset on early return
        } else {
            // Change the stack to its new contents if it hasn't been tampered with.
            if (this.getCount() == oldCount && Objects.equals(this.components.asPatch(), oldData)) {
                this.restorePatch(newData);
                this.setCount(newCount);
            }

            for (Map.Entry<BlockPos, BlockEntity> e : world.capturedTileEntities.entrySet()) {
                world.setBlockEntity(e.getValue());
            }

            for (BlockState blockstate : blocks) {
                int updateFlag = ((CraftBlockState) blockstate).getFlag();
                net.minecraft.world.level.block.state.BlockState oldBlock = ((CraftBlockState) blockstate).getHandle();
                BlockPos newblockposition = ((CraftBlockState) blockstate).getPosition();
                net.minecraft.world.level.block.state.BlockState block = world.getBlockState(newblockposition);

                if (!(block.getBlock() instanceof BaseEntityBlock)) { // Containers get placed automatically
                    block.onPlace(world, newblockposition, oldBlock, true);
                }

                world.notifyAndUpdatePhysics(newblockposition, null, oldBlock, block, world.getBlockState(newblockposition), updateFlag, 512); // send null chunk as chunk.k() returns false by this point
            }

            if (this.item == Items.WITHER_SKELETON_SKULL) { // Special case skulls to allow wither spawns to be cancelled
                BlockPos bp = blockpos;
                if (!world.getBlockState(blockpos).canBeReplaced()) {
                    if (!world.getBlockState(blockpos).isSolid()) {
                        bp = null;
                    } else {
                        bp = bp.relative(p_41662_.getClickedFace());
                    }
                }
                if (bp != null) {
                    BlockEntity te = world.getBlockEntity(bp);
                    if (te instanceof SkullBlockEntity) {
                        WitherSkullBlock.checkSpawn(world, bp, (SkullBlockEntity) te);
                    }
                }
            }

            // SPIGOT-4678
            if (this.item instanceof SignItem && SignItem.openSign != null) {
                try {
                    if (world.getBlockEntity(SignItem.openSign) instanceof SignBlockEntity tileentitysign) {
                        if (world.getBlockState(SignItem.openSign).getBlock() instanceof SignBlock blocksign) {
                            blocksign.openTextEdit(instance, tileentitysign, true, PlayerSignOpenEvent.Cause.PLACE); // Craftbukkit
                        }
                    }
                } finally {
                    SignItem.openSign = null;
                }
            }

            // SPIGOT-7315: Moved from BlockBed#setPlacedBy
            if (placeEvent != null && this.item instanceof BedItem) {
                BlockPos position = ((CraftBlock) placeEvent.getBlock()).getPosition();
                net.minecraft.world.level.block.state.BlockState blockData =  world.getBlockState(position);

                if (blockData.getBlock() instanceof BedBlock) {
                    world.blockUpdated(position, Blocks.AIR);
                    blockData.updateNeighbourShapes(world, position, 3);
                }
            }

            // SPIGOT-1288 - play sound stripped from ItemBlock
            if (this.item instanceof BlockItem) {
                SoundType soundeffecttype = ((BlockItem) this.item).getBlock().defaultBlockState().getSoundType(); // TODO: not strictly correct, however currently only affects decorated pots
                world.playSound(instance, blockpos, soundeffecttype.getPlaceSound(), SoundSource.BLOCKS, (soundeffecttype.getVolume() + 1.0F) / 2.0F, soundeffecttype.getPitch() * 0.8F);
            }

            instance.awardStat(Stats.ITEM_USED.get(item));
        }
    }

    @Unique
    private AtomicReference<ServerPlayer> neotenet$player = new AtomicReference<>();

    @Inject(method = "hurtAndBreak(ILnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Consumer;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;processDurabilityChange(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;I)I"))
    private void neotenet$bukkitHandleItemBreak(int p_220158_, ServerLevel p_346256_, LivingEntity p_220160_, Consumer<Item> p_348596_, CallbackInfo ci) {
        // CraftBukkit start
        if (!(p_220160_ instanceof ServerPlayer player)) {
            neotenet$player.set(((ServerPlayer) p_220160_));
            return;
        }
        if (p_220160_ != null) {
            PlayerItemDamageEvent event = new PlayerItemDamageEvent(player.getBukkitEntity(), CraftItemStack.asCraftMirror(((ItemStack) (Object) this)), p_220158_);
            event.getPlayer().getServer().getPluginManager().callEvent(event);

            if (p_220158_ != event.getDamage() || event.isCancelled()) {
                event.getPlayer().updateInventory();
            }
            if (event.isCancelled()) {
                return;
            }

            p_220158_ = event.getDamage();
        }
        // CraftBukkit end
    }

    @Inject(method = "hurtAndBreak(ILnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Consumer;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V"))
    private void neotenet$bukkitHandleItemBreak2(int p_220158_, ServerLevel p_346256_, LivingEntity p_220160_, Consumer<Item> p_348596_, CallbackInfo ci) {
        // CraftBukkit start - Check for item breaking
        if (this.count == 1 && neotenet$player.get() != null) {
            org.bukkit.craftbukkit.event.CraftEventFactory.callPlayerItemBreakEvent(neotenet$player.get(), ((ItemStack) (Object) this));
        }
        neotenet$player.set(null);
        // CraftBukkit end
    }

    // CraftBukkit start
    @Override
    public void restorePatch(DataComponentPatch datacomponentpatch) {
        this.components.restorePatch(datacomponentpatch);
    }
    // CraftBukkit end

    // CraftBukkit start
    @Deprecated
    public void setItem(Item item) {
        this.item = item;
    }
    // CraftBukkit end

    @ModifyExpressionValue(method = "consume", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hasInfiniteMaterials()Z"))
    private boolean neotenet$checkNotEmpty(boolean original) {
        return original && ((ItemStack) (Object) this) != ItemStack.EMPTY;
    }
}
