package org.teneted.neotenet.mixin.world.entity.animal.camel;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.camel.Camel;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.teneted.neotenet.mixin.world.entity.animal.horse.MixinAbstractHorse;

@Mixin(Camel.class)
public abstract class MixinCamel extends MixinAbstractHorse {

    protected MixinCamel(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    // @formatter:off
    @Shadow public abstract void standUpInstantly();
    // @formatter:on
}
