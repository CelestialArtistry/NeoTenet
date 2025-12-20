package org.taiyitistmc.bukkit;

import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.commands.CommandSourceStack;

import java.lang.reflect.Field;
import java.util.Map;

public class CommandNodeHooks {

    private static final Field CHILDREN_FIELD;
    private static final Field LITERALS_FIELD;
    private static final Field ARGUMENTS_FIELD;
    private static final Field CURRENT_COMMAND_FIELD;

    static {
        try {
            CHILDREN_FIELD = CommandNode.class.getDeclaredField("children");
            LITERALS_FIELD = CommandNode.class.getDeclaredField("literals");
            ARGUMENTS_FIELD = CommandNode.class.getDeclaredField("arguments");
            CURRENT_COMMAND_FIELD = CommandNode.class.getDeclaredField("CURRENT_COMMAND");
            
            // 设置字段可访问
            CHILDREN_FIELD.setAccessible(true);
            LITERALS_FIELD.setAccessible(true);
            ARGUMENTS_FIELD.setAccessible(true);
            CURRENT_COMMAND_FIELD.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static void removeCommand(CommandNode<?> node, String command) {
        try {
            ((Map<String, ?>) CHILDREN_FIELD.get(node)).remove(command);
            ((Map<String, ?>) LITERALS_FIELD.get(node)).remove(command);
            ((Map<String, ?>) ARGUMENTS_FIELD.get(node)).remove(command);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static CommandNode<?> getCurrent() {
        try {
            return (CommandNode<?>) CURRENT_COMMAND_FIELD.get(null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static <S> boolean canUse(CommandNode<S> node, S source) {
        if (source instanceof CommandSourceStack s) {
            try {
                s.currentCommand = node;
                return node.canUse(source);
            } finally {
                s.currentCommand = null;
            }
        } else {
            return node.canUse(source);
        }
    }
}