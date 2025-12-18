package com.taiyitistmc.injection.advancements;

public interface AdvancementHolderInjection {

    default org.bukkit.advancement.Advancement toBukkit() {
        throw new RuntimeException("Not Implemented");
    }
}
