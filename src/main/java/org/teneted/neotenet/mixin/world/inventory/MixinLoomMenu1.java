package org.teneted.neotenet.mixin.world.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.LoomMenu;
import org.bukkit.Location;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(targets = "net/minecraft/world/inventory/LoomMenu$1")
public abstract class MixinLoomMenu1 implements Container {

    @Shadow
    @Final
    LoomMenu this$0;

    @Override
    public Location getLocation() {
        return this$0.access.getLocation();
    }
}
