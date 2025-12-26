package org.teneted.neotenet.mixin.world.entity.animal;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Panda;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

@Mixin(Panda.class)
public abstract class MixinPanda extends Animal {

    @Shadow
    @Final
    static Predicate<ItemEntity> PANDA_ITEMS;

    protected MixinPanda(EntityType<? extends Animal> p_27557_, Level p_27558_) {
        super(p_27557_, p_27558_);
    }

    @ModifyExpressionValue(method = "pickUpItem", at = @At(value = "INVOKE", target = "Ljava/util/function/Predicate;test(Ljava/lang/Object;)Z"))
    private boolean neotenet$testEvent(boolean original, @Local(argsOnly = true) ItemEntity p_29121_) {
        return !CraftEventFactory.callEntityPickupItemEvent(this, p_29121_, 0, !(this.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty() && PANDA_ITEMS.test(p_29121_))).isCancelled();
    }

    @Inject(method = "pickUpItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;discard()V"))
    private void neotenet$removeCause(ItemEntity p_29121_, CallbackInfo ci) {
        p_29121_.pushRemoveCause(EntityRemoveEvent.Cause.PICKUP); // CraftBukkit - add Bukkit remove cause
    }
}
