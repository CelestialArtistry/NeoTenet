package org.teneted.neotenet.mixin.world.item.enchantment.effects;

import com.llamalad7.mixinextras.sugar.Cancellable;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.Ignite;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Ignite.class)
public class MixinIgnite {

    @Shadow
    @Final
    private LevelBasedValue duration;

    @Redirect(method = "apply", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;igniteForSeconds(F)V"))
    private void neotenet$resetApply(Entity instance, float v, @Local(argsOnly = true) EnchantedItemInUse p_346032_, @Local(argsOnly = true) Entity p_346370_, @Local(argsOnly = true) int p_344968_, @Cancellable CallbackInfo ci) {
        // CraftBukkit start - Call a combust event when somebody hits with a fire enchanted item
        EntityCombustEvent entityCombustEvent;
        if (p_346032_.owner() != null) {
            entityCombustEvent = new EntityCombustByEntityEvent(p_346032_.owner().getBukkitEntity(), p_346370_.getBukkitEntity(), this.duration.calculate(p_344968_));
        } else {
            entityCombustEvent = new EntityCombustEvent(p_346370_.getBukkitEntity(), this.duration.calculate(p_344968_));
        }

        org.bukkit.Bukkit.getPluginManager().callEvent(entityCombustEvent);
        if (entityCombustEvent.isCancelled()) {
            ci.cancel();
            return;
        }

        p_346370_.igniteForSeconds(entityCombustEvent.getDuration(), false);
        // CraftBukkit end
    }
}
