package org.taiyitistmc.injection.commands;

import com.mojang.brigadier.ParseResults;
import net.minecraft.commands.CommandSourceStack;

public interface InjectionCommands {

    default void dispatchServerCommand(CommandSourceStack sender, String command) {
        throw new RuntimeException("Not Implemented");
    }

    default void performPrefixedCommand(CommandSourceStack commandlistenerwrapper, String s, String label) {
        throw new RuntimeException("Not Implemented");
    }

    default void performCommand(ParseResults<CommandSourceStack> parseresults, String s, String label) {
        throw new RuntimeException("Not Implemented");
    }
}
