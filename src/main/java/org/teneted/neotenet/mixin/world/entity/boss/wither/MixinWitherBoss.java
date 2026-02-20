package org.teneted.neotenet.mixin.world.entity.boss.wither;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(WitherBoss.class)
public abstract class MixinWitherBoss extends Monster {

    protected MixinWitherBoss(EntityType<? extends Monster> p_33002_, Level p_33003_) {
        super(p_33002_, p_33003_);
    }

    @Redirect(method = "customServerAiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;explode(Lnet/minecraft/world/entity/Entity;DDDFZLnet/minecraft/world/level/Level$ExplosionInteraction;)Lnet/minecraft/world/level/Explosion;"))
    private Explosion neotenet$callExplosionPrimeEvent(Level instance, Entity entity, double x, double y, double z, float v, boolean b, Level.ExplosionInteraction explosionInteraction) {
        ExplosionPrimeEvent event = new ExplosionPrimeEvent(this.getBukkitEntity(), 7.0F, false);
        this.level().getCraftServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            return this.level().explode(this, this.getX(), this.getEyeY(), this.getZ(), event.getRadius(), event.getFire(), Level.ExplosionInteraction.MOB);
        }
        // CraftBukkit end
        return null;
    }

    @Redirect(method = "customServerAiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;globalLevelEvent(ILnet/minecraft/core/BlockPos;I)V"))
    private void neotenet$UseRelativeLocation(Level instance, int i, BlockPos pos, int j) {
        int viewDistance = ((ServerLevel) this.level()).getCraftServer().getViewDistance() * 16;
        for (ServerPlayer player : (List<ServerPlayer>) MinecraftServer.getServer().getPlayerList().players) {
            double deltaX = this.getX() - player.getX();
            double deltaZ = this.getZ() - player.getZ();
            double distanceSquared = deltaX * deltaX + deltaZ * deltaZ;
            if (distanceSquared > viewDistance * viewDistance) {
                double deltaLength = Math.sqrt(distanceSquared);
                double relativeX = player.getX() + (deltaX / deltaLength) * viewDistance;
                double relativeZ = player.getZ() + (deltaZ / deltaLength) * viewDistance;
                player.connection.send(new ClientboundLevelEventPacket(1023, new BlockPos((int) relativeX, (int) this.getY(), (int) relativeZ), 0, true));
            } else {
                player.connection.send(new ClientboundLevelEventPacket(1023, this.blockPosition(), 0, true));
            }
        }
        // CraftBukkit end
    }

    @Inject(method = "customServerAiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;destroyBlock(Lnet/minecraft/core/BlockPos;ZLnet/minecraft/world/entity/Entity;)Z"), cancellable = true)
    private void neotenet$callEntityChangeBlockEvent(CallbackInfo ci, @Local BlockPos blockposition) {
        // CraftBukkit start
        if (CraftEventFactory.callEntityChangeBlockEvent(this, blockposition, Blocks.AIR.defaultBlockState())) {
            ci.cancel();
        }
        // CraftBukkit end
    }

    @Inject(method = "customServerAiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/boss/wither/WitherBoss;heal(F)V", ordinal = 0))
    private void neotenet$pushHealReason(CallbackInfo ci) {
        this.pushHealReason(EntityRegainHealthEvent.RegainReason.WITHER_SPAWN);
    }

    @Inject(method = "customServerAiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/boss/wither/WitherBoss;heal(F)V", ordinal = 1))
    private void neotenet$pushHealReason0(CallbackInfo ci) {
        this.pushHealReason(EntityRegainHealthEvent.RegainReason.REGEN);
    }

    @Inject(method = "checkDespawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/boss/wither/WitherBoss;discard()V"))
    private void neotenet$pushdiscardReason(CallbackInfo ci) {
        this.pushRemoveCause(EntityRemoveEvent.Cause.DESPAWN); // CraftBukkit - add Bukkit remove cause
    }
}
