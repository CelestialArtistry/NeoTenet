package org.teneted.neotenet.injection.advancements;

public interface AdvancementHolderInjection {

    default org.bukkit.advancement.Advancement toBukkit() {
        throw new IllegalArgumentException("Not implemented");
    }
}