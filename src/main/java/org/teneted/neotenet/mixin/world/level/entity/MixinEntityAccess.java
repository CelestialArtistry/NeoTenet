package org.teneted.neotenet.mixin.world.level.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityAccess;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.teneted.neotenet.injection.world.level.entity.InjectionEntityAccess;

@Mixin(EntityAccess.class)
public interface MixinEntityAccess extends InjectionEntityAccess {

    @Shadow
    void setRemoved(Entity.RemovalReason p_156798_);

    // CraftBukkit start - add Bukkit remove cause
    @Override
    default void setRemoved(Entity.RemovalReason entity_removalreason, EntityRemoveEvent.Cause cause) {
        setRemoved(entity_removalreason);
    }
    // CraftBukkit end
}
