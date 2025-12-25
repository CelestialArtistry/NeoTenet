package org.teneted.neotenet.mixin.world.entity.animal;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Cow.class)
public abstract class MixinCow extends Animal {

    protected MixinCow(EntityType<? extends Animal> p_27557_, Level p_27558_) {
        super(p_27557_, p_27558_);
    }

    @Unique
    PlayerBucketFillEvent event;

    @Inject(method = "mobInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;playSound(Lnet/minecraft/sounds/SoundEvent;FF)V"))
    private void neotenet$callPlayerBucketFillEvent(Player p_28298_, InteractionHand p_28299_, CallbackInfoReturnable<InteractionResult> cir, @Local(ordinal = 0) ItemStack itemstack) {
        // CraftBukkit start - Got milk?
        event = CraftEventFactory.callPlayerBucketFillEvent((ServerLevel) p_28298_.level(), p_28298_, this.blockPosition(), this.blockPosition(), null, itemstack, Items.MILK_BUCKET, p_28299_);

        if (event.isCancelled()) {
            cir.setReturnValue(InteractionResult.PASS);
        }
        // CraftBukkit end
    }

    @ModifyArg(method = "mobInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemUtils;createFilledResult(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/ItemStack;"), index = 2)
    private ItemStack neotenet$useBukkit(ItemStack p_41814_) {
        return CraftItemStack.asNMSCopy(event.getItemStack());
    }
}
