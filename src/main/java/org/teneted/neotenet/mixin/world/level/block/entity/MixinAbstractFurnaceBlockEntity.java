package org.teneted.neotenet.mixin.world.level.block.entity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Cancellable;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.RecipeCraftingHolder;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.inventory.CraftItemType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.block.BlockExpEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.FurnaceStartSmeltEvent;
import org.bukkit.inventory.CookingRecipe;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.teneted.neotenet.injection.world.level.block.entity.InjectionAbstractFurnaceBlockEntity;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class MixinAbstractFurnaceBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, RecipeCraftingHolder, StackedContentsCompatible, InjectionAbstractFurnaceBlockEntity {

    @Shadow
    protected NonNullList<ItemStack> items;
    @Shadow
    @Final
    private Object2IntOpenHashMap<ResourceLocation> recipesUsed;

    @Shadow
    protected static boolean burn(RegistryAccess par1, RecipeHolder<?> par2, NonNullList<ItemStack> par3, int par4, AbstractFurnaceBlockEntity par5) {
        return false;
    }

    @Shadow
    public abstract List<RecipeHolder<?>> getRecipesToAwardAndPopExperience(ServerLevel p_154996_, Vec3 p_154997_);

    // CraftBukkit start - add fields and methods
    private int maxStack = MAX_STACK;
    public List<HumanEntity> transaction = new java.util.ArrayList<HumanEntity>();

    protected MixinAbstractFurnaceBlockEntity(BlockEntityType<?> p_155076_, BlockPos p_155077_, BlockState p_155078_) {
        super(p_155076_, p_155077_, p_155078_);
    }

    @Override
    public List<ItemStack> getContents() {
        return this.items;
    }

    @Override
    public void onOpen(CraftHumanEntity who) {
        transaction.add(who);
    }

    @Override
    public void onClose(CraftHumanEntity who) {
        transaction.remove(who);
    }

    @Override
    public List<HumanEntity> getViewers() {
        return transaction;
    }

    @Override
    public int getMaxStackSize() {
        return maxStack;
    }

    @Override
    public void setMaxStackSize(int size) {
        maxStack = size;
    }

    @Override
    public Object2IntOpenHashMap<ResourceLocation> getRecipesUsed() {
        return this.recipesUsed; // PAIL private -> public
    }
    // CraftBukkit end

    @Redirect(method = "serverTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity;getBurnDuration(Lnet/minecraft/world/item/ItemStack;)I"))
    private static int neotenet$callFurnaceBurnEvent(AbstractFurnaceBlockEntity instance,
                                                     ItemStack itemStack,
                                                     @Local(argsOnly = true) Level p_155014_,
                                                     @Local(argsOnly = true) BlockPos p_155015_,
                                                     @Local(argsOnly = true) AbstractFurnaceBlockEntity p_155017_,
                                                     @Cancellable CallbackInfo ci,
                                                     @Share("bukkitFurnaceBurnEvent") LocalRef<FurnaceBurnEvent> bukkitFurnaceBurnEvent) {
        // CraftBukkit start
        CraftItemStack fuel = CraftItemStack.asCraftMirror(itemStack);

        FurnaceBurnEvent furnaceBurnEvent = new FurnaceBurnEvent(CraftBlock.at(p_155014_, p_155015_), fuel, p_155017_.getBurnDuration(itemStack));
        p_155014_.getCraftServer().getPluginManager().callEvent(furnaceBurnEvent);
        bukkitFurnaceBurnEvent.set(furnaceBurnEvent);

        if (furnaceBurnEvent.isCancelled()) {
            ci.cancel();
        }
        return furnaceBurnEvent.getBurnTime();
    }

    @ModifyExpressionValue(method = "serverTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity;isLit()Z"))
    private static boolean neotenet$checkBurning(boolean original, @Share("bukkitFurnaceBurnEvent") LocalRef<FurnaceBurnEvent> bukkitFurnaceBurnEven) {
        return original && bukkitFurnaceBurnEven.get().isBurning();
    }

    @Inject(method = "serverTick", at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity;cookingProgress:I", ordinal = 0, opcode = Opcodes.GETFIELD))
    private static void neotenet$callFurnaceStartSmeltEvent(Level p_155014_, BlockPos p_155015_, BlockState p_155016_, AbstractFurnaceBlockEntity p_155017_, CallbackInfo ci, @Local RecipeHolder<?> recipeholder) {
        // CraftBukkit start
        if (recipeholder != null && p_155017_.cookingProgress == 0) {
            CraftItemStack source = CraftItemStack.asCraftMirror(p_155017_.getItemsPublic().get(0));
            CookingRecipe<?> recipe = (CookingRecipe<?>) recipeholder.toBukkitRecipe();

            FurnaceStartSmeltEvent event = new FurnaceStartSmeltEvent(CraftBlock.at(p_155014_, p_155015_), source, recipe);
            p_155014_.getCraftServer().getPluginManager().callEvent(event);

            p_155017_.cookingTotalTime = event.getTotalCookTime();
        }
        // CraftBukkit end
    }

    private static AtomicReference<Level> neotenet$level = new AtomicReference<>();
    private static AtomicReference<BlockPos> neotenet$blockPos = new AtomicReference<>();

    private static boolean burn(Level level, BlockPos blockPos, RegistryAccess p_266740_, @Nullable RecipeHolder<?> p_300910_, NonNullList<ItemStack> p_267073_, int p_267157_, AbstractFurnaceBlockEntity furnace) {
        neotenet$level.set(level);
        neotenet$blockPos.set(blockPos);
        return burn(p_266740_, p_300910_, p_267073_, p_267157_, furnace);
    }

    @Inject(method = "serverTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity;burn(Lnet/minecraft/core/RegistryAccess;Lnet/minecraft/world/item/crafting/RecipeHolder;Lnet/minecraft/core/NonNullList;ILnet/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity;)Z"))
    private static void neotenet$putInfos(Level p_155014_, BlockPos p_155015_, BlockState p_155016_, AbstractFurnaceBlockEntity p_155017_, CallbackInfo ci) {
        neotenet$level.set(p_155017_.getLevel());
        neotenet$blockPos.set(p_155017_.getBlockPos());
    }

    @Inject(method = "burn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"), cancellable = true)
    private static void neotenet$callFurnaceSmeltEvent(RegistryAccess p_266740_, RecipeHolder<?> p_300910_, NonNullList<ItemStack> p_267073_, int p_267157_, AbstractFurnaceBlockEntity furnace, CallbackInfoReturnable<Boolean> cir,
                                                       @Local(ordinal = 0) ItemStack itemstack,
                                                       @Local(ordinal = 1) ItemStack itemstack1,
                                                       @Local(ordinal = 2) ItemStack itemstack2) {
        // CraftBukkit start - fire FurnaceSmeltEvent
        CraftItemStack source = CraftItemStack.asCraftMirror(itemstack);
        org.bukkit.inventory.ItemStack result = CraftItemStack.asBukkitCopy(itemstack1);

        FurnaceSmeltEvent furnaceSmeltEvent = new FurnaceSmeltEvent(CraftBlock.at(neotenet$level.get(), neotenet$blockPos.get()), source, result);
        neotenet$level.get().getCraftServer().getPluginManager().callEvent(furnaceSmeltEvent);

        if (furnaceSmeltEvent.isCancelled()) {
            cir.setReturnValue(false);
        }

        result = furnaceSmeltEvent.getResult();
        itemstack1 = CraftItemStack.asNMSCopy(result);

        if (!itemstack1.isEmpty()) {
            if (itemstack2.isEmpty()) {
                p_267073_.set(2, itemstack1.copy());
            } else if (CraftItemStack.asCraftMirror(itemstack2).isSimilar(result)) {
                itemstack2.grow(itemstack1.getCount());
            } else {
                cir.setReturnValue(false);
            }
        }
    }

    @Inject(method = "getTotalCookTime", at = @At("HEAD"), cancellable = true)
    private static void neotenet$checkNull(Level p_222693_, AbstractFurnaceBlockEntity p_222694_, CallbackInfoReturnable<Integer> cir) {
        if (p_222693_ == null) cir.setReturnValue(200);
    }

    private static AtomicInteger neotenet$amount = new AtomicInteger();
    private static AtomicReference<BlockPos> neotenet$blockposition = new AtomicReference<>();
    private static AtomicReference<Player> neotenet$entityhuman = new AtomicReference<>();
    private static AtomicReference<ItemStack> neotenet$itemstack = new AtomicReference<>();

    @Override
    public void awardUsedRecipesAndPopExperience(ServerPlayer serverPlayer, ItemStack itemstack, int amount) { // CraftBukkit
        List<RecipeHolder<?>> list = this.getRecipesToAwardAndPopExperience(serverPlayer.serverLevel(), serverPlayer.position(), this.worldPosition, serverPlayer, itemstack, amount); // CraftBukkit
        serverPlayer.awardRecipes(list);

        for (RecipeHolder<?> recipeholder : list) {
            if (recipeholder != null) {
                serverPlayer.triggerRecipeCrafted(recipeholder, this.items);
            }
        }

        this.recipesUsed.clear();
    }

    @Inject(method = "awardUsedRecipesAndPopExperience", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity;getRecipesToAwardAndPopExperience(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;)Ljava/util/List;"))
    private void neotenet$putExpInfos(ServerPlayer p_155004_, CallbackInfo ci) {
        neotenet$amount.set(0);
        neotenet$blockposition.set(this.worldPosition);
        neotenet$entityhuman.set(null);
        neotenet$itemstack.set(null);
    }

    @Inject(method = "getRecipesToAwardAndPopExperience", at = @At("HEAD"))
    private void neotenet$putAllExpInfos(ServerLevel p_154996_, Vec3 p_154997_, CallbackInfoReturnable<List<RecipeHolder<?>>> cir) {
        neotenet$amount.set(0);
        neotenet$blockposition.set(this.worldPosition);
        neotenet$entityhuman.set(null);
        neotenet$itemstack.set(null);
    }

    @Override
    public List<RecipeHolder<?>> getRecipesToAwardAndPopExperience(ServerLevel p_154996_, Vec3 p_154997_, BlockPos blockposition, ServerPlayer entityplayer, ItemStack itemstack, int amount) {
        neotenet$amount.set(amount);
        neotenet$blockposition.set(blockposition);
        neotenet$entityhuman.set(entityplayer);
        neotenet$itemstack.set(itemstack);
        return getRecipesToAwardAndPopExperience(p_154996_, p_154997_);
    }

    @Inject(method = "createExperience", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ExperienceOrb;award(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;I)V"))
    private static void neotenet$callEvents(ServerLevel p_154999_, Vec3 p_155000_, int p_155001_, float p_155002_, CallbackInfo ci) {
        // CraftBukkit start - fire FurnaceExtractEvent / BlockExpEvent
        BlockExpEvent event;
        if (neotenet$amount.get() != 0) {
            event = new FurnaceExtractEvent((org.bukkit.entity.Player) neotenet$entityhuman.get().getBukkitEntity(), CraftBlock.at(p_154999_, neotenet$blockposition.get()), CraftItemType.minecraftToBukkit(neotenet$itemstack.get().getItem()), neotenet$amount.get(), p_155001_);
        } else {
            event = new BlockExpEvent(CraftBlock.at(p_154999_, neotenet$blockposition.get()), p_155001_);
        }
        p_154999_.getCraftServer().getPluginManager().callEvent(event);
        p_155001_ = event.getExpToDrop();
        // CraftBukkit end
    }

    private static void createExperience(ServerLevel p_154999_, Vec3 p_155000_, int p_155001_, float p_155002_, BlockPos blockposition, Player entityhuman, ItemStack itemstack, int amount) { // CraftBukkit
        // CraftBukkit start - fire FurnaceExtractEvent / BlockExpEvent
        neotenet$amount.set(amount);
        neotenet$blockposition.set(blockposition);
        neotenet$entityhuman.set(entityhuman);
        neotenet$itemstack.set(itemstack);
        int i = Mth.floor((float)p_155001_ * p_155002_);
        float f = Mth.frac((float)p_155001_ * p_155002_);
        if (f != 0.0F && Math.random() < (double)f) {
            i++;
        }
        BlockExpEvent event;
        if (amount != 0) {
            event = new FurnaceExtractEvent((org.bukkit.entity.Player) entityhuman.getBukkitEntity(), CraftBlock.at(p_154999_, blockposition), CraftItemType.minecraftToBukkit(itemstack.getItem()), amount, p_155001_);
        } else {
            event = new BlockExpEvent(CraftBlock.at(p_154999_, blockposition), p_155001_);
        }
        p_154999_.getCraftServer().getPluginManager().callEvent(event);
        p_155001_ = event.getExpToDrop();
        // CraftBukkit end
        ExperienceOrb.award(p_154999_, p_155000_, i);
    }
}