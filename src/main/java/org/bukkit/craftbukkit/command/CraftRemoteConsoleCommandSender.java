package org.bukkit.craftbukkit.command;

import java.net.SocketAddress;
import net.minecraft.network.chat.Component;
import net.minecraft.server.rcon.RconConsoleSourceCommandSource;
import org.bukkit.command.RemoteConsoleCommandSender;

public class CraftRemoteConsoleCommandSender extends ServerCommandSender implements RemoteConsoleCommandSender {

    private final RconConsoleSourceCommandSource listener;

    public CraftRemoteConsoleCommandSender(RconConsoleSourceCommandSource listener) {
        this.listener = listener;
    }

    public RconConsoleSourceCommandSource getListener() {
        return this.listener;
    }

    @Override
    public SocketAddress getAddress() {
       return this.listener.socketAddress;
    }

    @Override
    public void sendMessage(String message) {
        this.listener.sendSystemMessage(Component.literal(message + "\n")); // Send a newline after each message, to preserve formatting.
    }

    @Override
    public void sendMessage(String... messages) {
        for (String message : messages) {
            this.sendMessage(message);
        }
    }

    @Override
    public String getName() {
        return "Rcon";
    }

    @Override
    public boolean isOp() {
        return true;
    }

    @Override
    public void setOp(boolean value) {
        throw new UnsupportedOperationException("Cannot change operator status of remote controller.");
    }
}
