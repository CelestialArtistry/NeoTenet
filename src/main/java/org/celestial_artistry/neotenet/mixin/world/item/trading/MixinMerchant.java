package org.celestial_artistry.neotenet.mixin.world.item.trading;

import net.minecraft.world.item.trading.Merchant;
import org.bukkit.craftbukkit.inventory.CraftMerchant;
import org.spongepowered.asm.mixin.Mixin;
import org.celestial_artistry.neotenet.injection.world.item.trading.InjectionMerchant;

@Mixin(Merchant.class)
public interface MixinMerchant extends InjectionMerchant {

    @Override
    CraftMerchant getCraftMerchant();
}
