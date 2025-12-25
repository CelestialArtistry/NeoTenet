package org.teneted.neotenet.mixin.world.item;

import com.llamalad7.mixinextras.sugar.Cancellable;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.bukkit.craftbukkit.CraftEquipmentSlot;
import org.bukkit.entity.FishHook;
import org.bukkit.event.player.PlayerFishEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FishingRodItem.class)
public class MixinFishingRodItem {

    @Redirect(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"))
    private void neotenet$cancelPlaySound(Level instance, Player p_46543_, double p_46544_, double p_46545_, double p_46546_, SoundEvent p_46547_, SoundSource p_46548_, float p_46549_, float p_46550_) {
    }

    @Redirect(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean neotenet$callFishEvent(Level instance, Entity entity, @Local(argsOnly = true) Player p_41291_, @Local(argsOnly = true) InteractionHand p_41292_, @Cancellable CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir, @Local(ordinal = 0) ItemStack itemstack) {
        // CraftBukkit start
        PlayerFishEvent playerFishEvent = new PlayerFishEvent((org.bukkit.entity.Player) p_41291_.getBukkitEntity(), null, (FishHook) entity.getBukkitEntity(), CraftEquipmentSlot.getHand(p_41292_), PlayerFishEvent.State.FISHING);
        instance.getCraftServer().getPluginManager().callEvent(playerFishEvent);

        if (playerFishEvent.isCancelled()) {
            p_41291_.fishing = null;
            cir.setReturnValue(InteractionResultHolder.pass(itemstack));
        }
        instance.playSound((Player) null, p_41291_.getX(), p_41291_.getY(), p_41291_.getZ(), SoundEvents.FISHING_BOBBER_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (instance.getRandom().nextFloat() * 0.4F + 0.8F));
        instance.addFreshEntity(entity);
        // CraftBukkit end
        return true;
    }
}
