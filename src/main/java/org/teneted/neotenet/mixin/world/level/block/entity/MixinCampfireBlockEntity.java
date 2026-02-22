package org.teneted.neotenet.mixin.world.level.block.entity;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.block.BlockCookEvent;
import org.bukkit.event.block.CampfireStartEvent;
import org.bukkit.inventory.CampfireRecipe;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(CampfireBlockEntity.class)
public abstract class MixinCampfireBlockEntity extends BlockEntity {

    @Shadow
    public abstract Optional<RecipeHolder<CampfireCookingRecipe>> getCookableRecipe(ItemStack p_59052_);

    public MixinCampfireBlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    @Inject(method = "cookTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/Containers;dropItemStack(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/item/ItemStack;)V"), cancellable = true)
    private static void neotenet$callBlockCookEvent(Level p_155307_, BlockPos p_155308_, BlockState p_155309_, CampfireBlockEntity p_155310_, CallbackInfo ci, @Local(ordinal = 0) net.minecraft.world.item.ItemStack itemstack, @Local(ordinal = 1) net.minecraft.world.item.ItemStack itemstack1) {
        // CraftBukkit start - fire BlockCookEvent
        CraftItemStack source = CraftItemStack.asCraftMirror(itemstack);
        org.bukkit.inventory.ItemStack result = CraftItemStack.asBukkitCopy(itemstack1);

        BlockCookEvent blockCookEvent = new BlockCookEvent(CraftBlock.at(p_155307_, p_155308_), source, result);
        p_155307_.getCraftServer().getPluginManager().callEvent(blockCookEvent);

        if (blockCookEvent.isCancelled()) {
            ci.cancel();
            return;
        }

        result = blockCookEvent.getResult();
        itemstack1 = CraftItemStack.asNMSCopy(result);
        // CraftBukkit end
    }

    @Redirect(method = "placeFood", at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/block/entity/CampfireBlockEntity;cookingTime:[I", opcode = Opcodes.GETFIELD))
    private int[] neotenet$callCampfireStartEvent(CampfireBlockEntity instance, @Local(ordinal = 0, argsOnly = true) ItemStack p_238286_) {
        // CraftBukkit start
        CampfireStartEvent event = new CampfireStartEvent(CraftBlock.at(this.level,this.worldPosition), CraftItemStack.asCraftMirror(p_238286_), (CampfireRecipe) getCookableRecipe(p_238286_).get().toBukkitRecipe());
        this.level.getCraftServer().getPluginManager().callEvent(event);
        return new int[event.getTotalCookTime()]; // i -> event.getTotalCookTime()
        // CraftBukkit end
    }
}
