package org.teneted.neotenet.mixin.world.entity.animal;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.ShoulderRidingEntity;
import net.minecraft.world.level.Level;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShoulderRidingEntity.class)
public abstract class MixinShoulderRidingEntity extends TamableAnimal {

    protected MixinShoulderRidingEntity(EntityType<? extends TamableAnimal> p_21803_, Level p_21804_) {
        super(p_21803_, p_21804_);
    }

    @Inject(method = "setEntityOnShoulder", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/ShoulderRidingEntity;discard()V"))
    private void neotenet$pushRemoveCause(ServerPlayer p_29896_, CallbackInfoReturnable<Boolean> cir) {
        this.pushRemoveCause(EntityRemoveEvent.Cause.PICKUP); // CraftBukkit - add Bukkit remove cause
    }
}
