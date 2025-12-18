package com.taiyitistmc.injection.commands;

import net.minecraft.commands.CommandSourceStack;

public interface InjectionCommandSource {

    default org.bukkit.command.CommandSender getBukkitSender(CommandSourceStack wrapper) {
        throw new RuntimeException("Not Implemented");
    }

}
