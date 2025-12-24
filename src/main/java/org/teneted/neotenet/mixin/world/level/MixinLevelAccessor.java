package org.teneted.neotenet.mixin.world.level;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.neotenet.injection.world.level.InjectionLevelAccessor;

@Mixin(LevelAccessor.class)
public interface MixinLevelAccessor extends InjectionLevelAccessor {

    @Override
    ServerLevel getMinecraftWorld(); // CraftBukkit
}
