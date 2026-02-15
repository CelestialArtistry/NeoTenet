package org.teneted.neotenet.mixin.world.entity.projectile;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SmallFireball.class)
public abstract class MixinSmallFireball extends Fireball {

    public MixinSmallFireball(EntityType<? extends Fireball> p_37006_, Level p_37007_) {
        super(p_37006_, p_37007_);
    }

    @Inject(method = "<init>(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/phys/Vec3;)V", at = @At("RETURN"))
    private void neotenet$init(Level p_37375_, LivingEntity p_37376_, Vec3 p_347501_, CallbackInfo ci) {
        // CraftBukkit start
        if (this.getOwner() != null && this.getOwner() instanceof Mob) {
            isIncendiary = this.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
        }
        // CraftBukkit end
    }

    @WrapWithCondition(method = "onHitEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;igniteForSeconds(F)V"))
    private boolean neotenet$callEntityCombustByEntityEvent(Entity instance, float v) {
        EntityCombustByEntityEvent event = new EntityCombustByEntityEvent((org.bukkit.entity.Projectile) this.getBukkitEntity(), instance.getBukkitEntity(), 5.0F);
        instance.level().getCraftServer().getPluginManager().callEvent(event);
        return !event.isCancelled();
    }

    @ModifyExpressionValue(method = "onHitBlock", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/event/EventHooks;canEntityGrief(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean neotenet$checkIf(boolean original) {
        return original && isIncendiary;
    }

    @ModifyExpressionValue(method = "onHitBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isEmptyBlock(Lnet/minecraft/core/BlockPos;)Z"))
    private boolean neotenet$callBlockIgniteEvent(boolean original, @Local BlockPos blockpos) {
       return original && !org.bukkit.craftbukkit.event.CraftEventFactory.callBlockIgniteEvent(this.level(), blockpos, this).isCancelled();
    }

    @Inject(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/SmallFireball;discard()V"))
    private void neotenet$removeCause(HitResult p_37388_, CallbackInfo ci) {
        this.pushRemoveCause(EntityRemoveEvent.Cause.HIT);
    }
}
