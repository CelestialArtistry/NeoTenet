package org.taiyitistmc.injection.commands.arguments;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.selector.EntitySelector;

import java.io.StringReader;

public interface InjectionEntityArgument {

    default EntitySelector parse(StringReader stringreader, boolean flag, boolean overridePermissions) throws CommandSyntaxException {
        throw new RuntimeException("Not Implemented");
    }
}
