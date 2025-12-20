package org.taiyitistmc.injection.commands;

public interface InjectionCommandNode {

    default void removeCommand(String name) {
        throw new RuntimeException("Not Implemented");
    }
}
