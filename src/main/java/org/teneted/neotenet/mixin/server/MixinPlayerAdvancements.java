package org.teneted.neotenet.mixin.server;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerAdvancements.class)
public class MixinPlayerAdvancements {

    @Shadow
    private ServerPlayer player;

    @Inject(method = "lambda$applyFrom$0", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"), cancellable = true)
    private void neotenet$applyFrom(ServerAdvancementManager p_301166_, ResourceLocation p_300732_, AdvancementProgress p_300733_, CallbackInfo ci) {
        if (!p_300732_.getNamespace().equals("minecraft")) ci.cancel(); return; // CraftBukkit
    }

    @Inject(method = "award", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/AdvancementRewards;grant(Lnet/minecraft/server/level/ServerPlayer;)V"))
    private void neotenet$award(AdvancementHolder p_300979_, String p_135990_, CallbackInfoReturnable<Boolean> cir) {
        this.player.level().getCraftServer().getPluginManager().callEvent(new org.bukkit.event.player.PlayerAdvancementDoneEvent(this.player.getBukkitEntity(), p_300979_.toBukkit())); // CraftBukkit
    }
}
