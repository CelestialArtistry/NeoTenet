package org.taiyitistmc.mixin.server.dedicated;

import net.minecraft.server.dedicated.DedicatedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DedicatedServer.class)
public class MixinDedicatedServer {

    @Inject(method = "initServer", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;info(Ljava/lang/String;Ljava/lang/Object;)V", ordinal = 3))
    private void taiyist$log(CallbackInfoReturnable<Boolean> cir) {
        System.out.println("TaiyiTistMC is Done!");
    }

    @Inject(at = @At("HEAD"), method = "showGui", cancellable = true)
    public void taiyitist$nogui(CallbackInfo ci) {
        ci.cancel();
    }
}
