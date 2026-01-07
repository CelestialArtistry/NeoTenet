package org.teneted.neotenet.mixin.world.entity.raid;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.raid.Raider;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Raider.class)
public class MixinRaider {

    @Inject(method = "pickUpItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;discard()V"))
    private void neotenet$discardCause(ItemEntity p_37866_, CallbackInfo ci) {
        p_37866_.pushRemoveCause(EntityRemoveEvent.Cause.PICKUP);
    }
}
