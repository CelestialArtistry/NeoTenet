/**
 * Automatically generated file, changes will be lost.
 */
package org.bukkit.craftbukkit.block.impl;

public final class CraftTallPlant extends org.bukkit.craftbukkit.block.data.CraftBlockData implements org.bukkit.block.data.Bisected {

    public CraftTallPlant() {
        super();
    }

    public CraftTallPlant(net.minecraft.world.level.block.state.BlockState state) {
        super(state);
    }

    // org.bukkit.craftbukkit.block.data.CraftBisected

    private static final net.minecraft.world.level.block.state.properties.EnumProperty<?> HALF = getEnum(net.minecraft.world.level.block.DoublePlantBlock.class, "half");

    @Override
    public Half getHalf() {
        return this.get(CraftTallPlant.HALF, Half.class);
    }

    @Override
    public void setHalf(Half half) {
        this.set(CraftTallPlant.HALF, half);
    }
}
