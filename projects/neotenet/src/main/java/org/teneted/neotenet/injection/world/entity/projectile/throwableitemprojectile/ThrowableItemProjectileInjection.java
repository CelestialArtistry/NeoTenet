package org.teneted.neotenet.injection.world.entity.projectile.throwableitemprojectile;

import net.minecraft.world.item.Item;

public interface ThrowableItemProjectileInjection {

    default Item getDefaultItemPublic() {
        throw new IllegalArgumentException("Not implemented");
    }
}
