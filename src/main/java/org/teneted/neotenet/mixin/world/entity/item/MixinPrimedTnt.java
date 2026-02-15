package org.teneted.neotenet.mixin.world.entity.item;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.entity.Explosive;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PrimedTnt.class)
public abstract class MixinPrimedTnt extends Entity {

    public MixinPrimedTnt(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/PrimedTnt;discard()V"))
    private void neotenet$cancelDiscard(PrimedTnt instance) {}

    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/Level;isClientSide:Z", opcode = Opcodes.GETFIELD))
    private void neotenet$discard(CallbackInfo ci) {
        this.discard();
        this.pushRemoveCause(EntityRemoveEvent.Cause.EXPLODE);
    }

    @WrapOperation(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;explode(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;Lnet/minecraft/world/level/ExplosionDamageCalculator;DDDFZLnet/minecraft/world/level/Level$ExplosionInteraction;)Lnet/minecraft/world/level/Explosion;"))
    private Explosion neotenet$explode(Level instance, Entity entity, DamageSource damageSource, ExplosionDamageCalculator explosionDamageCalculator, double x, double y, double z, float v, boolean b, Level.ExplosionInteraction explosionInteraction, Operation<Explosion> original) {
        ExplosionPrimeEvent event = CraftEventFactory.callExplosionPrimeEvent((Explosive) this.getBukkitEntity());
        if (!event.isCancelled()) {
            return original.call(instance, entity, damageSource, explosionDamageCalculator, x, y, z, event.getRadius(), event.getFire(), explosionInteraction);
        } else {
            return null;
        }
    }
}
