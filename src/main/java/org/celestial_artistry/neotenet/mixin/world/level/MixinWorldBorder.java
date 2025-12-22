package org.celestial_artistry.neotenet.mixin.world.level;

import net.minecraft.world.level.border.BorderChangeListener;
import net.minecraft.world.level.border.WorldBorder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(WorldBorder.class)
public class MixinWorldBorder {

    @Shadow
    @Final
    private List<BorderChangeListener> listeners;

    @Inject(method = "addListener", at = @At("HEAD"), cancellable = true)
    private void taiyitist$checkContain(BorderChangeListener p_61930_, CallbackInfo ci) {
        if (listeners.contains(p_61930_)) ci.cancel(); // CraftBukkit
    }

}
