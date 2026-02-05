package org.teneted.neotenet.mixin.world.entity.monster;

import net.minecraft.world.entity.monster.SpellcasterIllager;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.world.entity.monster.SpellcasterIllager.SpellcasterUseSpellGoal")
public abstract class MixinSpellcasterIllager_SpellcasterUseSpellGoal {

    @Shadow
    @Final
    private SpellcasterIllager this$0;

    @Shadow
    protected abstract SpellcasterIllager.IllagerSpell getSpell();

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/SpellcasterIllager$SpellcasterUseSpellGoal;performSpellCasting()V"), cancellable = true)
    private void neotenet$handleEntitySpellCastEvent(CallbackInfo ci) {
        // CraftBukkit start
        if (!CraftEventFactory.handleEntitySpellCastEvent(this$0, this.getSpell())) {
            ci.cancel();
            return;
        }
        // CraftBukkit end
    }
}
