package org.teneted.neotenet.mixin.world.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.StonecutterMenu;
import org.bukkit.Location;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(targets = "net/minecraft/world/inventory/StonecutterMenu$1")
public abstract class MixinStonecutterMenu1 implements Container {

    @Shadow
    @Final
    StonecutterMenu this$0;

    @Override
    public Location getLocation() {
        return this$0.access.getLocation();
    }
}
