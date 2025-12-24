package org.teneted.neotenet.injection.advancements;

public interface InjectionAdvancementHolder {

    default org.bukkit.advancement.Advancement toBukkit() {
        throw new RuntimeException("Not Implemented");
    }
}
