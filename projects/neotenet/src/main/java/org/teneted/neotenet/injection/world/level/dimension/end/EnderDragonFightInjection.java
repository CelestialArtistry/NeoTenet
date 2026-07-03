package org.teneted.neotenet.injection.world.level.dimension.end;

import net.minecraft.world.entity.boss.enderdragon.EndCrystal;

import java.util.List;

public interface EnderDragonFightInjection {

    default boolean respawnDragonBoolean(List<EndCrystal> crystals) {
        throw new IllegalArgumentException("Not implemented");
    }
}
