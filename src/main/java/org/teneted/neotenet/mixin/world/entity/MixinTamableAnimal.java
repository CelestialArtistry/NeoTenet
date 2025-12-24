package org.teneted.neotenet.mixin.world.entity;

import com.llamalad7.mixinextras.sugar.Cancellable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import org.bukkit.Location;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TamableAnimal.class)
public abstract class MixinTamableAnimal extends Animal implements OwnableEntity {

    protected MixinTamableAnimal(EntityType<? extends Animal> p_27557_, Level p_27558_) {
        super(p_27557_, p_27558_);
    }

    @Redirect(method = "maybeTeleportTo", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/TamableAnimal;moveTo(DDDFF)V"))
    private void neotenet$tpEvent(TamableAnimal instance, double x, double y, double z, float xRot, float yRot, @Cancellable CallbackInfoReturnable<Boolean> cir) {
        // CraftBukkit start
        EntityTeleportEvent event = CraftEventFactory.callEntityTeleportEvent(((TamableAnimal) (Object) this), (double) x + 0.5D, (double) y, (double) z + 0.5D);
        if (event.isCancelled()) {
            cir.setReturnValue(false);
        }
        Location to = event.getTo();
        this.moveTo(to.getX(), to.getY(), to.getZ(), to.getYaw(), to.getPitch());
        // CraftBukkit end
    }
}
