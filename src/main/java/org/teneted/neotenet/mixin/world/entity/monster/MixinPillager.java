package org.teneted.neotenet.mixin.world.entity.monster;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Pillager;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Pillager.class)
public class MixinPillager {

    @Inject(method = "pickUpItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;discard()V"))
    private void neotenet$discardReason(ItemEntity p_33296_, CallbackInfo ci) {
        p_33296_.pushRemoveCause(EntityRemoveEvent.Cause.PICKUP); // CraftBukkit - add Bukkit remove cause
    }
}
