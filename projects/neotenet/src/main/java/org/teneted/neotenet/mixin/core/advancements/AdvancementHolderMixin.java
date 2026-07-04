package org.teneted.neotenet.mixin.core.advancements;

import net.minecraft.advancements.AdvancementHolder;
import org.bukkit.advancement.Advancement;
import org.bukkit.craftbukkit.advancement.CraftAdvancement;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.neotenet.injection.advancements.AdvancementHolderInjection;

@Mixin(AdvancementHolder.class)
public class AdvancementHolderMixin implements AdvancementHolderInjection {

    @Override
    public Advancement toBukkit() {
        return new CraftAdvancement(((AdvancementHolder) (Object) this));
    }
}
