package org.taiyitistmc.mixin.commands;

import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.taiyitistmc.injection.commands.InjectionCommandNode;

import java.util.Map;
import java.util.function.Predicate;

@Mixin(value = CommandNode.class, remap = false)
public class MixinCommandNode<S> implements InjectionCommandNode {

    // @formatter:off
    @Shadow @Final private Map<String, CommandNode<S>> children;
    @Shadow @Final private Map<String, LiteralCommandNode<S>> literals;
    @Shadow @Final private Map<String, ArgumentCommandNode<S, ?>> arguments;
    @Shadow @Final private Predicate<S> requirement;
    // @formatter:on

    @Override
    public void removeCommand(String name) {
        children.remove(name);
        literals.remove(name);
        arguments.remove(name);
    }

    @Overwrite
    public boolean canUse(final S source) {
        if (source instanceof final CommandSourceStack commandSourceStack) {
            try {
                commandSourceStack.currentCommand = ((CommandNode<?>) (Object) this);
                return requirement.test(source);
            } finally {
                commandSourceStack.currentCommand = null;
            }
        }
        // CraftBukkit end
        return requirement.test(source);
    }
}
