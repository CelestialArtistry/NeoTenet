package org.teneted.neotenet.injection.commands;

import net.minecraft.commands.CommandSourceStack;

public interface CommandSourceInjection {

    default org.bukkit.command.CommandSender getBukkitSender(CommandSourceStack wrapper) {
        throw new IllegalArgumentException("Not implemented");
    }
}