package org.taiyitistmc.mixin.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerboundUseItemPacket.class)
public class MixinServerboundUseItemPacket {

    @Shadow
    public long timestamp;

    @Inject(method = "<init>(Lnet/minecraft/network/FriendlyByteBuf;)V", at = @At("RETURN"))
    private void taiyitist$read(FriendlyByteBuf buf, CallbackInfo ci) {
        this.timestamp = System.currentTimeMillis();
    }
}
