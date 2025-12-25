package org.teneted.neotenet.mixin.world.entity.animal.armadillo;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.armadillo.Armadillo;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Armadillo.class)
public abstract class MixinArmadillo extends Animal {

    protected MixinArmadillo(EntityType<? extends Animal> p_27557_, Level p_27558_) {
        super(p_27557_, p_27558_);
    }

    @Inject(method = "customServerAiStep",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/animal/armadillo/Armadillo;spawnAtLocation(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/entity/item/ItemEntity;"))
    private void neotenet$forceDrops0(CallbackInfo ci) {
        this.forceDrops = true; // CraftBukkit

    }

    @Inject(method = "customServerAiStep",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/animal/armadillo/Armadillo;gameEvent(Lnet/minecraft/core/Holder;)V"))
    private void neotenet$forceDrops1(CallbackInfo ci) {
        this.forceDrops = true; // CraftBukkit

    }
}
