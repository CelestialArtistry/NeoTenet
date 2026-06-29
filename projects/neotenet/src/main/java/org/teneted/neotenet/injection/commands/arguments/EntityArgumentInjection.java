package org.teneted.neotenet.injection.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.selector.EntitySelector;

public interface EntityArgumentInjection {

    default EntitySelector parse(StringReader reader, boolean allowSelectors, boolean overridePermissions) throws CommandSyntaxException {
        throw new IllegalArgumentException("Not implemented");
    }
}
