package org.teneted.neotenet.mixin.bukkit;

import net.neoforged.neoforge.internal.BrandingControl;
import org.bukkit.craftbukkit.CraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = CraftServer.class, remap = false)
public class CraftServerMixin {

    /**
     * @author wdog5734
     * @reason NeoTenet
     */
    @Overwrite
    public String getName() {
        return BrandingControl.getServerBranding() + " with "+ "NeoTenet";
    }
}
