package org.celestial_artistry.neotenet.injection.commands;

public interface InjectionCommandNode {

    default void removeCommand(String name) {
        throw new RuntimeException("Not Implemented");
    }
}
