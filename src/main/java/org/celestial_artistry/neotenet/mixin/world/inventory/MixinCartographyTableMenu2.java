package org.celestial_artistry.neotenet.mixin.world.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.CartographyTableMenu;
import org.bukkit.Location;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(targets = "net/minecraft/world/inventory/CartographyTableMenu$2")
public abstract class MixinCartographyTableMenu2 implements Container {

    @Shadow
    @Final
    CartographyTableMenu this$0;

    @Override
    public Location getLocation() {
        return this$0.access.getLocation();
    }
}
