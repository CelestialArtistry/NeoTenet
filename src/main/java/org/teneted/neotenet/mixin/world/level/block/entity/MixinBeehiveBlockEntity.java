package org.teneted.neotenet.mixin.world.level.block.entity;

import com.google.common.collect.Lists;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(BeehiveBlockEntity.class)
public abstract class MixinBeehiveBlockEntity {

    @Shadow
    public int maxBees;

    @Shadow
    @Final
    private List<BeehiveBlockEntity.BeeData> stored;

    @Shadow
    protected abstract List<Entity> releaseAllOccupants(BlockState p_58760_, BeehiveBlockEntity.BeeReleaseStatus p_58761_);

    @Shadow
    protected static boolean releaseOccupant(Level p_155137_, BlockPos p_155138_, BlockState p_155139_, BeehiveBlockEntity.Occupant p_332184_, @org.jetbrains.annotations.Nullable List<Entity> p_155141_, BeehiveBlockEntity.BeeReleaseStatus p_155142_, @org.jetbrains.annotations.Nullable BlockPos p_155143_) {
        return false;
    }

    @ModifyReturnValue(method = "isFull", at = @At("RETURN"))
    private boolean neotenet$useBukkitMaxBees(boolean original) {
        return this.stored.size() == this.maxBees; // CraftBukkit
    }

    @Inject(method = "emptyAllLivingFromHive", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/Bee;setTarget(Lnet/minecraft/world/entity/LivingEntity;)V"))
    private void neotenet$targetReason(Player p_58749_, BlockState p_58750_, BeehiveBlockEntity.BeeReleaseStatus p_58751_, CallbackInfo ci, @Local Bee bee) {
        bee.bridge$pushGoalTargetReason(org.bukkit.event.entity.EntityTargetEvent.TargetReason.CLOSEST_PLAYER, true);
    }

    @Definition(id = "stored", field = "Lnet/minecraft/world/level/block/entity/BeehiveBlockEntity;stored:Ljava/util/List;")
    @Definition(id = "size", method = "Ljava/util/List;size()I")
    @Expression("this.stored.size() < 3")
    @ModifyExpressionValue(method = "addOccupant", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean neotenet$useMaxBees(boolean original) {
        return this.stored.size() < this.maxBees;
    }

    private static AtomicBoolean neotenet$markForce = new AtomicBoolean(false);

    private List<Entity> releaseAllOccupants(BlockState p_58760_, BeehiveBlockEntity.BeeReleaseStatus p_58761_, boolean force) {
        neotenet$markForce.set(force);
        return releaseAllOccupants(p_58760_, p_58761_);
    }

    @Inject(method = "saveAdditional", at = @At("RETURN"))
    private void neotenet$saveMaxBees(CompoundTag p_187467_, HolderLookup.Provider p_324426_, CallbackInfo ci) {
        // CraftBukkit start
        if (p_187467_.contains("Bukkit.MaxEntities")) {
            this.maxBees = p_187467_.getInt("Bukkit.MaxEntities");
        }
        // CraftBukkit end
    }

    @Inject(method = "releaseOccupant", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private static void neotenet$pushSpawnReason(Level p_155137_, BlockPos p_155138_, BlockState p_155139_, BeehiveBlockEntity.Occupant p_332184_, List<Entity> p_155141_, BeehiveBlockEntity.BeeReleaseStatus p_155142_, BlockPos p_155143_, CallbackInfoReturnable<Boolean> cir, @Local Entity entity) {
        entity.pushSpawnCause(CreatureSpawnEvent.SpawnReason.BEEHIVE);
    }

    @Inject(method = "tickOccupants", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/BeehiveBlockEntity;releaseOccupant(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/entity/BeehiveBlockEntity$Occupant;Ljava/util/List;Lnet/minecraft/world/level/block/entity/BeehiveBlockEntity$BeeReleaseStatus;Lnet/minecraft/core/BlockPos;)Z"))
    private static void neotenet$ticksInHive(Level p_155150_, BlockPos p_155151_, BlockState p_155152_, List<BeehiveBlockEntity.BeeData> p_155153_, BlockPos p_155154_, CallbackInfo ci, @Local BeehiveBlockEntity.BeeData beehiveblockentity$beedata, @Local BeehiveBlockEntity.BeeReleaseStatus beehiveblockentity$beereleasestatus) {
        if (!releaseOccupant(
                p_155150_, p_155151_, p_155152_, beehiveblockentity$beedata.toOccupant(), null, beehiveblockentity$beereleasestatus, p_155154_
        )) {
            beehiveblockentity$beedata.setTicksInHive(beehiveblockentity$beedata.getOccupant().minTicksInHive() / 2);// Not strictly Vanilla behaviour in cases where bees cannot spawn but still reasonable
            // CraftBukkit end
        }
    }
    private static boolean releaseOccupant(
            Level p_155137_,
            BlockPos p_155138_,
            BlockState p_155139_,
            BeehiveBlockEntity.Occupant p_332184_,
            @Nullable List<Entity> p_155141_,
            BeehiveBlockEntity.BeeReleaseStatus p_155142_,
            @Nullable BlockPos p_155143_,
            boolean force
    ) {
        neotenet$markForce.set(force);
        return releaseOccupant(p_155137_, p_155138_, p_155139_, p_332184_, p_155141_, p_155142_, p_155143_);
    }

    @Inject(method = "releaseOccupant", at = @At("HEAD"), cancellable = true)
    private static void neotenet$checkIfForce(Level p_155137_, BlockPos p_155138_, BlockState p_155139_, BeehiveBlockEntity.Occupant p_332184_, List<Entity> p_155141_, BeehiveBlockEntity.BeeReleaseStatus p_155142_, BlockPos p_155143_, CallbackInfoReturnable<Boolean> cir) {
        if (!neotenet$markForce.get()) {
            cir.setReturnValue(false);
        }
    }

    @Redirect(method = "loadAdditional", at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/block/entity/BeehiveBlockEntity;stored:Ljava/util/List;"))
    private List neotenet$useNewList(BeehiveBlockEntity instance) {
        return Lists.newArrayList(); // CraftBukkit - SPIGOT-7790: create new copy (may be modified in physics event triggered by honey change)
    }

    @Redirect(method = "applyImplicitComponents", at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/block/entity/BeehiveBlockEntity;stored:Ljava/util/List;"))
    private List neotenet$resetLinkList(BeehiveBlockEntity instance) {
        return Lists.newArrayList(); // CraftBukkit - SPIGOT-7790: create new copy (may be modified in physics event triggered by honey change)
    }
}
