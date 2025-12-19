package org.taiyitistmc.mixin.advancements;

import net.minecraft.advancements.AdvancementHolder;
import org.bukkit.craftbukkit.advancement.CraftAdvancement;
import org.spongepowered.asm.mixin.Mixin;
import org.taiyitistmc.injection.advancements.InjectionAdvancementHolder;

@Mixin(AdvancementHolder.class)
public class MixinAdvancementHolder implements InjectionAdvancementHolder {

    @Override
    // CraftBukkit start
    public final org.bukkit.advancement.Advancement toBukkit() {
        return new CraftAdvancement(((AdvancementHolder) (Object) this));
    }
    // CraftBukkit end
}
