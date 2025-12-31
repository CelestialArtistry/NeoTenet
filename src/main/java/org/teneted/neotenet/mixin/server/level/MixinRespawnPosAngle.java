package org.teneted.neotenet.mixin.server.level;

import net.minecraft.server.level.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.teneted.neotenet.injection.server.level.InjectionRespawnPosAngle;

@Mixin(ServerPlayer.RespawnPosAngle.class)
public abstract class MixinRespawnPosAngle implements InjectionRespawnPosAngle {

    @Shadow
    @Final
    private boolean isBedSpawn;
    @Shadow
    @Final
    private boolean isAnchorSpawn;


    @Override
    public boolean isBedSpawn() {
        return this.isBedSpawn;
    }

    @Override
    public boolean isAnchorSpawn() {
        return this.isAnchorSpawn;
    }
}
