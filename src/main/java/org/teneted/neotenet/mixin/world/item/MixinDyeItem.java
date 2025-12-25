package org.teneted.neotenet.mixin.world.item;

import com.llamalad7.mixinextras.sugar.Cancellable;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import org.bukkit.event.entity.SheepDyeWoolEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DyeItem.class)
public class MixinDyeItem {

    @Shadow
    @Final
    private DyeColor dyeColor;

    @Redirect(method = "interactLivingEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/Sheep;setColor(Lnet/minecraft/world/item/DyeColor;)V"))
    private void neotenet$callSheepDyeWoolEvent(Sheep instance, DyeColor p_29856_, @Local(argsOnly = true) Player p_41086_, @Cancellable CallbackInfoReturnable<InteractionResult> cir) {
        // CraftBukkit start
        byte bColor = (byte) this.dyeColor.getId();
        SheepDyeWoolEvent event = new SheepDyeWoolEvent((org.bukkit.entity.Sheep) instance.getBukkitEntity(), org.bukkit.DyeColor.getByWoolData(bColor), (org.bukkit.entity.Player) p_41086_.getBukkitEntity());
        instance.level().getCraftServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            cir.setReturnValue(InteractionResult.PASS);
        }

        instance.setColor(DyeColor.byId((byte) event.getColor().getWoolData()));
        // CraftBukkit end
    }

}
