package org.teneted.neotenet.mixin.world.entity.raid;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Cancellable;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raids;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(Raids.class)
public class MixinRaids {

    @WrapOperation(method = "createOrExtendRaid", at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))
    private <K, V> Object neotenet$callRaidTriggerEvent(Map instance, K k, V v, Operation<Object> original, @Local Raid raid, @Local(argsOnly = true) ServerPlayer p_37964_, @Cancellable CallbackInfoReturnable<Raid> cir) {
        if (!CraftEventFactory.callRaidTriggerEvent(raid, p_37964_)) {
            p_37964_.removeEffect(MobEffects.RAID_OMEN);
            cir.setReturnValue(null);
        }
        if (raid.isStarted() || (raid.isInProgress() && raid.getRaidOmenLevel() < raid.getMaxRaidOmenLevel())) {
            return original.call(instance, k, v);
        } else {
            return null;
        }
    }
}
