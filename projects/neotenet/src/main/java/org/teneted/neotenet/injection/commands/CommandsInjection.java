package org.teneted.neotenet.injection.commands;

import com.mojang.brigadier.ParseResults;
import net.minecraft.commands.CommandSourceStack;

public interface CommandsInjection {

    default void dispatchServerCommand(CommandSourceStack sender, String command) {
        throw new IllegalArgumentException("Not implemented");
    }

    default void performPrefixedCommand(CommandSourceStack sender, String command, String label) {
        throw new IllegalArgumentException("Not implemented");
    }

    default void performCommand(ParseResults<CommandSourceStack> command, String commandString, String label) {
        throw new IllegalArgumentException("Not implemented");
    }
}