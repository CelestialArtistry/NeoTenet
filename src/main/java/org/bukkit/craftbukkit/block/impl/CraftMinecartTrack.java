/**
 * Automatically generated file, changes will be lost.
 */
package org.bukkit.craftbukkit.block.impl;

public final class CraftMinecartTrack extends org.bukkit.craftbukkit.block.data.CraftBlockData implements org.bukkit.block.data.Rail, org.bukkit.block.data.Waterlogged {

    public CraftMinecartTrack() {
        super();
    }

    public CraftMinecartTrack(net.minecraft.world.level.block.state.BlockState state) {
        super(state);
    }

    // org.bukkit.craftbukkit.block.data.CraftRail

    private static final net.minecraft.world.level.block.state.properties.EnumProperty<?> SHAPE = getEnum(net.minecraft.world.level.block.RailBlock.class, "shape");

    @Override
    public Shape getShape() {
        return this.get(CraftMinecartTrack.SHAPE, Shape.class);
    }

    @Override
    public void setShape(Shape shape) {
        this.set(CraftMinecartTrack.SHAPE, shape);
    }

    @Override
    public java.util.Set<Shape> getShapes() {
        return this.getValues(CraftMinecartTrack.SHAPE, Shape.class);
    }

    // org.bukkit.craftbukkit.block.data.CraftWaterlogged

    private static final net.minecraft.world.level.block.state.properties.BooleanProperty WATERLOGGED = getBoolean(net.minecraft.world.level.block.RailBlock.class, "waterlogged");

    @Override
    public boolean isWaterlogged() {
        return this.get(CraftMinecartTrack.WATERLOGGED);
    }

    @Override
    public void setWaterlogged(boolean waterlogged) {
        this.set(CraftMinecartTrack.WATERLOGGED, waterlogged);
    }
}
