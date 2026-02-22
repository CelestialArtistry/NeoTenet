package org.teneted.neotenet.mixin.world.level.block.entity;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ConduitBlockEntity;
import net.minecraft.world.phys.AABB;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ConduitBlockEntity.class)
public class MixinConduitBlockEntity {

    @Inject(method = "applyEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z"))
    private static void neotenet$pushEffectCause(Level p_155444_, BlockPos p_155445_, List<BlockPos> p_155446_, CallbackInfo ci, @Local Player player) {
        player.pushEffectCause(EntityPotionEffectEvent.Cause.CONDUIT);
    }

    private static void applyEffects(Level p_155444_, BlockPos p_155445_, int j) { // j = effect range in blocks
        int k = p_155445_.getX();
        int l = p_155445_.getY();
        int i1 = p_155445_.getZ();
        AABB aabb = new AABB((double)k, (double)l, (double)i1, (double)(k + 1), (double)(l + 1), (double)(i1 + 1))
                .inflate((double)j)
                .expandTowards(0.0, (double)p_155444_.getHeight(), 0.0);
        List<Player> list = p_155444_.getEntitiesOfClass(Player.class, aabb);
        if (!list.isEmpty()) {
            for (Player player : list) {
                if (p_155445_.closerThan(player.blockPosition(), (double)j) && player.isInWaterOrRain()) {
                    player.pushEffectCause(EntityPotionEffectEvent.Cause.CONDUIT);
                    player.addEffect(new MobEffectInstance(MobEffects.CONDUIT_POWER, 260, 0, true, true));
                }
            }
        }
    }

    @Definition(id = "p_155413_", local = @Local(type = ConduitBlockEntity.class, argsOnly = true))
    @Definition(id = "destroyTarget", field = "Lnet/minecraft/world/level/block/entity/ConduitBlockEntity;destroyTarget:Lnet/minecraft/world/entity/LivingEntity;")
    @Expression("p_155413_.destroyTarget != null")
    @ModifyExpressionValue(method = "updateDestroyTarget", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static boolean neotenet$markTrueCheck(boolean original) {
        return true && original;
    }

    @WrapWithCondition(method = "updateDestroyTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"))
    private static boolean neotenet$checkHurt(Level instance, Player player, double x, double y, double z, SoundEvent soundEvent, SoundSource soundSource, float i, float v,
                                              @Local(argsOnly = true) ConduitBlockEntity p_155413_,
                                              @Local(argsOnly = true) Level p_155409_) {
        return p_155413_.destroyTarget.hurt(p_155409_.damageSources().magic(), 4.0F);
    }

    @Redirect(method = "updateDestroyTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private static boolean neotenet$cancelHurt(LivingEntity instance, DamageSource damageSource, float v) {
        return false;
    }
}
