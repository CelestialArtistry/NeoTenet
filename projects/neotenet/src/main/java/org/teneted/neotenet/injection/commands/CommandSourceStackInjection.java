package org.teneted.neotenet.injection.commands;

public interface CommandSourceStackInjection {

    default org.bukkit.command.CommandSender getBukkitSender() {
        throw new IllegalArgumentException("Not implemented");
    }
}
