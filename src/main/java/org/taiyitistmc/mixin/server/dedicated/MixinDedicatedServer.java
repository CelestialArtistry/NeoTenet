package org.taiyitistmc.mixin.server.dedicated;

import net.minecraft.server.dedicated.DedicatedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DedicatedServer.class)
public class MixinDedicatedServer {

    @Inject(at = @At("HEAD"), method = "showGui", cancellable = true)
    public void taiyitist$nogui(CallbackInfo ci) {
        ci.cancel();
    }
}
