package org.teneted.neotenet.mixin.world.entity.ai.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SitWhenOrderedToGoal.class)
public class MixinSitWhenOrderedToGoal {

    @Shadow
    @Final
    private TamableAnimal mob;

    /**
     * @author wdog5
     * @reason bukkit
     */
    @Overwrite
    public boolean canUse() {
        if (!this.mob.isTame()) {
            return this.mob.isOrderedToSit() && this.mob.getTarget() == null; // CraftBukkit - Allow sitting for wild animals
        } else if (this.mob.isInWaterOrBubble()) {
            return false;
        } else if (!this.mob.onGround()) {
            return false;
        } else {
            LivingEntity livingentity = this.mob.getOwner();
            if (livingentity == null) {
                return true;
            } else {
                return this.mob.distanceToSqr(livingentity) < 144.0 && livingentity.getLastHurtByMob() != null ? false : this.mob.isOrderedToSit();
            }
        }
    }
}
