package org.teneted.neotenet.mixin.world.entity.projectile;

import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.teneted.neotenet.injection.world.entity.projectile.InjectionThrowableItemProjectile;

@Mixin(ThrowableItemProjectile.class)
public abstract class MixinThrowableItemProjectile implements InjectionThrowableItemProjectile {

    @Shadow
    protected abstract Item getDefaultItem();

    // CraftBukkit start
    @Override
    public Item getDefaultItemPublic() {
        return getDefaultItem();
    }
    /* CraftBukkit end */
}
