package org.teneted.neotenet.injection.world.entity.ai.gossip;

import net.minecraft.world.entity.ai.gossip.GossipType;
import net.minecraft.world.entity.npc.villager.Villager;

import java.util.UUID;
import java.util.function.Predicate;

public interface GossipContainerInjection {

    default int getReputation(UUID entity, Predicate<GossipType> types, boolean weighted) {
        throw new IllegalArgumentException("Not implemented");
    }

    default void add(UUID target, GossipType type, int amountToAdd, org.bukkit.entity.Villager.ReputationEvent changeReason) {
        throw new IllegalArgumentException("Not implemented");
    }

    default void set(UUID target, GossipType type, int amount, org.bukkit.entity.Villager.ReputationEvent changeReason) {
        throw new IllegalArgumentException("Not implemented");
    }

    default void remove(UUID target, GossipType type, org.bukkit.entity.Villager.ReputationEvent changeReason) {
        throw new IllegalArgumentException("Not implemented");
    }

    default void remove(GossipType type, org.bukkit.entity.Villager.ReputationEvent changeReason) {
        throw new IllegalArgumentException("Not implemented");
    }

    default void decay(Villager villager, UUID uuid) {
        throw new IllegalArgumentException("Not implemented");
    }

    default void remove(UUID uuid, GossipType gossipType, int amount, org.bukkit.entity.Villager.ReputationEvent changeReason) {
        throw new IllegalArgumentException("Not implemented");
    }
}
