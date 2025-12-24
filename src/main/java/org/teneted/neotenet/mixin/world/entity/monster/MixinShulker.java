package org.teneted.neotenet.mixin.world.entity.monster;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.level.Level;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Shulker.class)
public abstract class MixinShulker extends AbstractGolem {

    @Shadow @Final protected static EntityDataAccessor<Byte> DATA_PEEK_ID;

    protected MixinShulker(EntityType<? extends AbstractGolem> entityType, Level level) {
        super(entityType, level);
    }

    // @formatter:off
    @Shadow @Nullable protected abstract Direction findAttachableSurface(BlockPos p_149811_);

    @Shadow
    public abstract void setAttachFace(Direction p_149789_);
    // @formatter:on

    @Inject(method = "hitByShulkerBullet", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private void neotenet$breedCause(CallbackInfo ci) {
        this.level().pushAddEntityReason(CreatureSpawnEvent.SpawnReason.BREEDING);
    }
}
