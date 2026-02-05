package org.teneted.neotenet.mixin.world.entity.monster;

import com.llamalad7.mixinextras.sugar.Cancellable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.level.Level;
import org.bukkit.event.entity.EntityTargetEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.UUID;

@Mixin(ZombifiedPiglin.class)
public abstract class MixinZombifiedPiglin  extends Zombie {

    @Shadow
    @Nullable
    public abstract UUID getPersistentAngerTarget();

    @Shadow
    public abstract void setPersistentAngerTarget(@org.jetbrains.annotations.Nullable UUID p_34444_);

    @Shadow
    @Final
    private static UniformInt PERSISTENT_ANGER_TIME;

    public MixinZombifiedPiglin(Level p_34274_) {
        super(p_34274_);
    }

    @Inject(method = "lambda$alertOthers$3", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/ZombifiedPiglin;setTarget(Lnet/minecraft/world/entity/LivingEntity;)V"))
    private void neotenet$pushTargetReason(ZombifiedPiglin p_325816_, CallbackInfo ci) {
        p_325816_.bridge$pushGoalTargetReason(EntityTargetEvent.TargetReason.TARGET_ATTACKED_NEARBY_ENTITY, true);
    }

    @ModifyArg(method = "startPersistentAngerTimer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/ZombifiedPiglin;setRemainingPersistentAngerTime(I)V"), index = 0)
    private int neotenet$callPigZombieAngerEvent(int p_34448_, @Cancellable CallbackInfo ci) {
        // CraftBukkit start
        Entity entity = ((ServerLevel) this.level()).getEntity(getPersistentAngerTarget());
        org.bukkit.event.entity.PigZombieAngerEvent event = new org.bukkit.event.entity.PigZombieAngerEvent((org.bukkit.entity.PigZombie) this.getBukkitEntity(), (entity == null) ? null : entity.getBukkitEntity(), PERSISTENT_ANGER_TIME.sample(this.random));
        this.level().getCraftServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            this.setPersistentAngerTarget(null);
            ci.cancel();
        }
        return event.getNewAnger();
    }
}
