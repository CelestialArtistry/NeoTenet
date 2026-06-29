package org.teneted.neotenet.injection.server.bossevents;

import org.bukkit.boss.KeyedBossBar;

public interface CustomBossEventInjection {

    default KeyedBossBar getBukkitEntity() {
        throw new IllegalArgumentException("Not implemented");
    }
}
