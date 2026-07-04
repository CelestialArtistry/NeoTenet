package org.teneted.neotenet.mixin.core.world;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.neotenet.injection.world.ContainerInjection;

@Mixin(Container.class)
public interface ContainerMixin extends ContainerInjection {

    // CraftBukkit start
    @Override
    java.util.List<ItemStack> getContents();

    @Override
    void onOpen(CraftHumanEntity who);

    @Override
    void onClose(CraftHumanEntity who);

    @Override
    java.util.List<org.bukkit.entity.HumanEntity> getViewers();

    @Override
    org.bukkit.inventory.InventoryHolder getOwner();

    @Override
    void setMaxStackSize(int size);

    @Override
    org.bukkit.Location getLocation();
    // CraftBukkit end
}
