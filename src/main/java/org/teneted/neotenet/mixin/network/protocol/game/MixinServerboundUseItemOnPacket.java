package org.teneted.neotenet.mixin.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerboundUseItemOnPacket.class)
public class MixinServerboundUseItemOnPacket {

    @Shadow
    public long timestamp;

    @Inject(method = "<init>(Lnet/minecraft/network/FriendlyByteBuf;)V", at = @At("RETURN"))
    private void neotenet$read(FriendlyByteBuf buf, CallbackInfo ci) {
        this.timestamp = System.currentTimeMillis();
    }
}
