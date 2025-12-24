package org.teneted.neotenet.injection.commands;

import com.mojang.brigadier.ParseResults;
import net.minecraft.commands.CommandSourceStack;

public interface InjectionCommands {

    default void performCommandCB(ParseResults<CommandSourceStack> parseresults, String s, String label, boolean throwCommandError) {
        throw new RuntimeException("Not Implemented");
    }

    default void performCommandCB(ParseResults<CommandSourceStack> pParseResults, String pCommand, String label) { // CraftBukkit
        throw new RuntimeException("Not Implemented");
    }

    default void dispatchServerCommand(CommandSourceStack sender, String command) {
        throw new RuntimeException("Not Implemented");
    }

    default void performPrefixedCommand(CommandSourceStack commandlistenerwrapper, String s, String label) {
        throw new RuntimeException("Not Implemented");
    }
}
