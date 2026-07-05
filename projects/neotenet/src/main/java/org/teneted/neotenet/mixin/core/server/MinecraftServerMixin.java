package org.teneted.neotenet.mixin.core.server;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.neotenet.injection.server.MinecraftServerInjection;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin implements MinecraftServerInjection {
}