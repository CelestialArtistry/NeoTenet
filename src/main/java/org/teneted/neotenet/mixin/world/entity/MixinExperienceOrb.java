package org.teneted.neotenet.mixin.world.entity;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ExperienceOrb.class)
public abstract class MixinExperienceOrb extends Entity {

    public MixinExperienceOrb(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Definition(id = "takeXpDelay", field = "Lnet/minecraft/world/entity/player/Player;takeXpDelay:I")
    @Definition(id = "p_20792_", local = @Local(type = Player.class, argsOnly = true))
    @Expression(value = "p_20792_.takeXpDelay == 0")
    @ModifyExpressionValue(method = "playerTouch", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean neotenet$callPlayerPickupExperienceEvent(boolean original, @Local(ordinal = 0) ServerPlayer serverplayer) {
        return original && new com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent(serverplayer.getBukkitEntity(), (org.bukkit.entity.ExperienceOrb) this.getBukkitEntity()).callEvent();
    }
}
