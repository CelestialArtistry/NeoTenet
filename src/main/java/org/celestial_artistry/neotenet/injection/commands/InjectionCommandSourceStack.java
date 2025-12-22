package org.celestial_artistry.neotenet.injection.commands;

public interface InjectionCommandSourceStack {

    default boolean hasPermission(int i, String bukkitPermission) {
        throw new RuntimeException("Not Implemented");
    }

    default org.bukkit.command.CommandSender getBukkitSender() {
        throw new RuntimeException("Not Implemented");
    }
}
