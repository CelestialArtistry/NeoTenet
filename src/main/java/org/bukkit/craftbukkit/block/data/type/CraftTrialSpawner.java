package org.bukkit.craftbukkit.block.data.type;

import org.bukkit.block.data.type.TrialSpawner;
import org.bukkit.craftbukkit.block.data.CraftBlockData;

public abstract class CraftTrialSpawner extends CraftBlockData implements TrialSpawner {

    private static final net.minecraft.world.level.block.state.properties.EnumProperty<?> TRIAL_SPAWNER_STATE = getEnum("trial_spawner_state");
    private static final net.minecraft.world.level.block.state.properties.BooleanProperty OMINOUS = getBoolean("ominous");

    @Override
    public State getTrialSpawnerState() {
        return this.get(CraftTrialSpawner.TRIAL_SPAWNER_STATE, State.class);
    }

    @Override
    public void setTrialSpawnerState(State state) {
        this.set(CraftTrialSpawner.TRIAL_SPAWNER_STATE, state);
    }

    @Override
    public boolean isOminous() {
        return this.get(CraftTrialSpawner.OMINOUS);
    }

    @Override
    public void setOminous(boolean ominous) {
        this.set(CraftTrialSpawner.OMINOUS, ominous);
    }
}
