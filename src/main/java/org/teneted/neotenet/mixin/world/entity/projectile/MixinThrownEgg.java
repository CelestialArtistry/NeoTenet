package org.teneted.neotenet.mixin.world.entity.projectile;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThrownEgg.class)
public abstract class MixinThrownEgg extends ThrowableItemProjectile {

    public MixinThrownEgg(EntityType<? extends ThrowableItemProjectile> p_37442_, Level p_37443_) {
        super(p_37442_, p_37443_);
    }

    @Definition(id = "random", field = "Lnet/minecraft/world/entity/projectile/ThrownEgg;random:Lnet/minecraft/util/RandomSource;")
    @Definition(id = "nextInt", method = "Lnet/minecraft/util/RandomSource;nextInt(I)I")
    @Expression("this.random.nextInt(32) == 0")
    @ModifyExpressionValue(method = "onHit", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean neotenet$resetCheck(boolean original, @Local int i) {
        // CraftBukkit start
        org.bukkit.entity.EntityType hatchingType = org.bukkit.entity.EntityType.CHICKEN;

        Entity shooter = this.getOwner();
        boolean hatching = this.random.nextInt(8) == 0;
        if (!hatching) {
            i = 0;
        }
        if (shooter instanceof ServerPlayer) {
            PlayerEggThrowEvent event = new PlayerEggThrowEvent((org.bukkit.entity.Player) shooter.getBukkitEntity(), (org.bukkit.entity.Egg) this.getBukkitEntity(), hatching, (byte) i, hatchingType);
            this.level().getCraftServer().getPluginManager().callEvent(event);

            i = event.getNumHatches();
            hatching = event.isHatching();
            hatchingType = event.getHatchingType();
            // If hatching is set to false, ensure child count is 0
            if (!hatching) {
                i = 0;
            }
        }
        // CraftBukkit end
        return true;
    }

    @WrapWithCondition(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/Chicken;setAge(I)V"))
    private boolean neotenet$checkAgeable(Chicken instance, int i) {
        return instance.getBukkitEntity() instanceof org.bukkit.entity.Ageable;
    }

    @Inject(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private void neotenet$spawnReason(HitResult p_37488_, CallbackInfo ci) {
        this.pushSpawnCause(CreatureSpawnEvent.SpawnReason.EGG);
    }

    @Inject(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/ThrownEgg;discard()V"))
    private void neotenet$discardReason(HitResult p_37488_, CallbackInfo ci) {
        this.pushRemoveCause(EntityRemoveEvent.Cause.HIT);
    }
}
