package org.bukkit.craftbukkit.block.data;

import org.bukkit.block.data.Bisected;

public class CraftBisected extends CraftBlockData implements Bisected {

    private static final net.minecraft.world.level.block.state.properties.EnumProperty<?> HALF = getEnum("half");

    @Override
    public Half getHalf() {
        return this.get(CraftBisected.HALF, Half.class);
    }

    @Override
    public void setHalf(Half half) {
        this.set(CraftBisected.HALF, half);
    }
}
