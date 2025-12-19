package org.bukkit.craftbukkit.block.data;

import org.bukkit.block.data.Rail;

public abstract class CraftRail extends CraftBlockData implements Rail {

    private static final net.minecraft.world.level.block.state.properties.EnumProperty<?> SHAPE = getEnum("shape");

    @Override
    public Shape getShape() {
        return this.get(CraftRail.SHAPE, Shape.class);
    }

    @Override
    public void setShape(Shape shape) {
        this.set(CraftRail.SHAPE, shape);
    }

    @Override
    public java.util.Set<Shape> getShapes() {
        return this.getValues(CraftRail.SHAPE, Shape.class);
    }
}
