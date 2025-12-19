package org.taiyitistmc.mixin.world.entity.moster;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTransformEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(net.minecraft.world.entity.monster.Zombie.class)
public abstract class MixinZombie extends Monster {

    protected MixinZombie(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "convertToZombieType", at = @At("HEAD"))
    private void taiyitist$transformReason(EntityType<? extends net.minecraft.world.entity.monster.Zombie> entityType, CallbackInfo ci) {
        this.bridge$pushTransformReason(EntityTransformEvent.TransformReason.DROWNED);
        this.level().pushAddEntityReason(CreatureSpawnEvent.SpawnReason.DROWNED);
    }

    @Inject(method = "convertToZombieType", locals = LocalCapture.CAPTURE_FAILHARD, at = @At("RETURN"))
    private void taiyitist$stopConversion(EntityType<? extends net.minecraft.world.entity.monster.Zombie> entityType, CallbackInfo ci) {
        if (entityType == null) {
            ((Zombie) this.getBukkitEntity()).setConversionTime(-1);
        }
    }

    @Inject(method = "hurt", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/Zombie;setTarget(Lnet/minecraft/world/entity/LivingEntity;)V"))
    private void taiyitist$spawnWithReason(DamageSource damageSource, float f, CallbackInfoReturnable<Boolean> cir, ServerLevel serverLevel, LivingEntity livingEntity, int i, int j, int k, net.minecraft.world.entity.monster.Zombie zombie, int l, int m, int n, int o, BlockPos blockPos, EntityType entityType) {
        serverLevel.pushAddEntityReason(CreatureSpawnEvent.SpawnReason.REINFORCEMENTS);
        if (livingEntity != null) {
            zombie.bridge$pushGoalTargetReason(EntityTargetEvent.TargetReason.REINFORCEMENT_TARGET, true);
        }
    }

    @Inject(method = "finalizeSpawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/ServerLevelAccessor;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private void taiyitist$mount(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, SpawnGroupData spawnGroupData, CallbackInfoReturnable<SpawnGroupData> cir) {
        serverLevelAccessor.getLevel().pushAddEntityReason(CreatureSpawnEvent.SpawnReason.MOUNT);
    }
}
