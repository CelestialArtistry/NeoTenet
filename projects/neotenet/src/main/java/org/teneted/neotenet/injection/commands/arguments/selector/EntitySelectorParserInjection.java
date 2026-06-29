package org.teneted.neotenet.injection.commands.arguments.selector;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.selector.EntitySelector;

public interface EntitySelectorParserInjection {

    default EntitySelector parse(boolean overridePermissions) throws CommandSyntaxException {
        throw new IllegalArgumentException("Not implemented");
    }
}
