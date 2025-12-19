package org.bukkit.craftbukkit.block.data.type;

import org.bukkit.block.data.type.StructureBlock;
import org.bukkit.craftbukkit.block.data.CraftBlockData;

public abstract class CraftStructureBlock extends CraftBlockData implements StructureBlock {

    private static final net.minecraft.world.level.block.state.properties.EnumProperty<?> MODE = getEnum("mode");

    @Override
    public Mode getMode() {
        return this.get(CraftStructureBlock.MODE, Mode.class);
    }

    @Override
    public void setMode(Mode mode) {
        this.set(CraftStructureBlock.MODE, mode);
    }
}
