package org.teneted.neotenet.mixin.server.rcon;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.rcon.RconConsoleSource;
import org.bukkit.craftbukkit.command.CraftRemoteConsoleCommandSender;
import org.teneted.neotenet.injection.server.rcon.InjectionRconConsoleSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(RconConsoleSource.class)
public abstract class MixinRconConsoleSource implements CommandSource, InjectionRconConsoleSource {

    @Shadow
    @Final
    private CraftRemoteConsoleCommandSender remoteConsole;

    @Shadow
    @Final
    private StringBuffer buffer;

    // CraftBukkit start - Send a String
    public void sendMessage(String message) {
        this.buffer.append(message);
    }

    @Override
    public org.bukkit.command.CommandSender getBukkitSender(CommandSourceStack wrapper) {
        return this.remoteConsole;
    }
    // CraftBukkit end
}
