package org.teneted.neotenet.mixin.world.entity.boss.enderdragon.phases;

import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonPhaseInstance;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhaseManager;
import org.bukkit.craftbukkit.entity.CraftEnderDragon;
import org.bukkit.event.entity.EnderDragonChangePhaseEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(EnderDragonPhaseManager.class)
public class MixinEnderDragonPhaseManager {

    @Shadow
    @Final
    private EnderDragon dragon;

    @Shadow
    @Nullable
    private DragonPhaseInstance currentPhase;

    @Inject(method = "setPhase",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/boss/enderdragon/phases/DragonPhaseInstance;getPhase()Lnet/minecraft/world/entity/boss/enderdragon/phases/EnderDragonPhase;"), cancellable = true)
    private void neotenet$callEnderDragonChangePhaseEvent(EnderDragonPhase<?> p_31417_, CallbackInfo ci) {
        // CraftBukkit start - Call EnderDragonChangePhaseEvent
        EnderDragonChangePhaseEvent event = new EnderDragonChangePhaseEvent(
                (CraftEnderDragon) this.dragon.getBukkitEntity(),
                (this.currentPhase == null) ? null : CraftEnderDragon.getBukkitPhase(this.currentPhase.getPhase()),
                CraftEnderDragon.getBukkitPhase(p_31417_)
        );
        this.dragon.level().getCraftServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            ci.cancel();
            return;
        }
        p_31417_ = CraftEnderDragon.getMinecraftPhase(event.getNewPhase());
        // CraftBukkit end
    }
}
