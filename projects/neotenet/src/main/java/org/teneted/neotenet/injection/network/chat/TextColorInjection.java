package org.teneted.neotenet.injection.network.chat;

import net.minecraft.ChatFormatting;

public interface TextColorInjection {

    default ChatFormatting getFormat() {
        throw new IllegalArgumentException("Not implemented");
    }
}