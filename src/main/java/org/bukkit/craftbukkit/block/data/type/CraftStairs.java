package org.bukkit.craftbukkit.block.data.type;

import org.bukkit.block.data.type.Stairs;
import org.bukkit.craftbukkit.block.data.CraftBlockData;

public abstract class CraftStairs extends CraftBlockData implements Stairs {

    private static final net.minecraft.world.level.block.state.properties.EnumProperty<?> SHAPE = getEnum("shape");

    @Override
    public Shape getShape() {
        return this.get(CraftStairs.SHAPE, Shape.class);
    }

    @Override
    public void setShape(Shape shape) {
        this.set(CraftStairs.SHAPE, shape);
    }
}
