package org.teneted.neotenet.mixin.world.level.storage.loot.predicates;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(ExplosionCondition.class)
public class MixinExplosionCondition {

    /**
     * @author wdog5
     * @reason bukkit
     */
    @Overwrite
    public boolean test(LootContext p_81659_) {
        Float f = p_81659_.getParamOrNull(LootContextParams.EXPLOSION_RADIUS);
        if (f != null) {
            RandomSource randomsource = p_81659_.getRandom();
            float f1 = 1.0F / f;
            return randomsource.nextFloat() < f1;   // CraftBukkit - <= to < to allow for plugins to completely disable block drops from explosions
        } else {
            return true;
        }
    }

}
