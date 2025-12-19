package org.taiyitistmc.mixin.server.dedicated;

import net.minecraft.server.dedicated.DedicatedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DedicatedServer.class)
public class MixinDedicatedServer {

    @Inject(method = "initServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/dedicated/DedicatedServer;initializeKeyPair()V"))
    private void taiyist$log(CallbackInfoReturnable<Boolean> cir) {
        System.out.println("TaiyiTistMC is Loading...");
    }

    @Inject(at = @At("HEAD"), method = "showGui", cancellable = true)
    public void taiyitist$nogui(CallbackInfo ci) {
        ci.cancel();
    }
}
