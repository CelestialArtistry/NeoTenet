package org.teneted.neotenet.mixin.world.entity.projectile;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.EyeOfEnder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EyeOfEnder.class)
public abstract class MixinEyeOfEnder extends Entity {

    @Shadow
    private boolean surviveAfterDeath;

    public MixinEyeOfEnder(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/EyeOfEnder;discard()V"))
    private void neotenet$discardReason(CallbackInfo ci) {
        this.pushRemoveCause(this.surviveAfterDeath ? EntityRemoveEvent.Cause.DROP : EntityRemoveEvent.Cause.DESPAWN);
    }

    @ModifyArg(method = "readAdditionalSaveData", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/EyeOfEnder;setItem(Lnet/minecraft/world/item/ItemStack;)V"), index = 0)
    private ItemStack neotenet$resetStack(ItemStack p_36973_) {
        return !p_36973_.isEmpty() ? p_36973_ : ItemStack.EMPTY;
    }
}
