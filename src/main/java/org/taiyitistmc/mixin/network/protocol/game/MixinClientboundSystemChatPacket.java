package org.taiyitistmc.mixin.network.protocol.game;

import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ClientboundSystemChatPacket.class)
public class MixinClientboundSystemChatPacket {

    private String a;

    public String content0() {
        return a;
    }

}
