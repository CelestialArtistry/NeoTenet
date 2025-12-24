package org.teneted.neotenet.mixin.world.level;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelWriter;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.neotenet.injection.world.level.InjectionLevelWriter;

@Mixin(LevelWriter.class)
public interface MixinLevelWriter extends InjectionLevelWriter {

    // CraftBukkit start
    @Override
    default boolean addFreshEntity(Entity entity, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason reason) {
        return false;
    }
    // CraftBukkit end
}
