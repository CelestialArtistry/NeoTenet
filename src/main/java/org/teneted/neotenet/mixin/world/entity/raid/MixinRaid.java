package org.teneted.neotenet.mixin.world.entity.raid;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import org.bukkit.event.raid.RaidStopEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.teneted.neotenet.injection.world.entity.raid.InjectionRaid;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Mixin(Raid.class)
public class MixinRaid implements InjectionRaid {

    @Shadow
    private Raid.RaidStatus status;

    @Shadow
    @Final
    private Map<Integer, Set<Raider>> groupRaiderMap;

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/raid/Raid;stop()V", ordinal = 0))
    private void neotenet$callRaidStopEvent(CallbackInfo ci) {
        org.bukkit.craftbukkit.event.CraftEventFactory.callRaidStopEvent(((Raid) (Object) this), org.bukkit.event.raid.RaidStopEvent.Reason.PEACE); // CraftBukkit
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/raid/Raid;stop()V", ordinal = 1))
    private void neotenet$callRaidStopEvent0(CallbackInfo ci) {
        org.bukkit.craftbukkit.event.CraftEventFactory.callRaidStopEvent(((Raid) (Object) this), org.bukkit.event.raid.RaidStopEvent.Reason.NOT_IN_VILLAGE); // CraftBukkit
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/raid/Raid;stop()V", ordinal = 2))
    private void neotenet$callRaidStopEvent1(CallbackInfo ci) {
        org.bukkit.craftbukkit.event.CraftEventFactory.callRaidStopEvent(((Raid) (Object) this), org.bukkit.event.raid.RaidStopEvent.Reason.TIMEOUT); // CraftBukkit
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/raid/Raid;stop()V", ordinal = 3))
    private void neotenet$callRaidStopEvent2(CallbackInfo ci) {
        org.bukkit.craftbukkit.event.CraftEventFactory.callRaidStopEvent(((Raid) (Object) this), RaidStopEvent.Reason.UNSPAWNABLE); // CraftBukkit
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/raid/Raid;stop()V", ordinal = 4))
    private void neotenet$callRaidStopEvent3(CallbackInfo ci) {
        org.bukkit.craftbukkit.event.CraftEventFactory.callRaidStopEvent(((Raid) (Object) this), RaidStopEvent.Reason.FINISHED); // CraftBukkit
    }

    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/raid/Raid$RaidStatus;LOSS:Lnet/minecraft/world/entity/raid/Raid$RaidStatus;", shift = At.Shift.AFTER))
    private void neotenet$callRaidFinishEvent(CallbackInfo ci) {
        org.bukkit.craftbukkit.event.CraftEventFactory.callRaidFinishEvent(((Raid) (Object) this), new java.util.ArrayList<>()); // CraftBukkit
    }

    @Inject(method = "joinRaid", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntityWithPassengers(Lnet/minecraft/world/entity/Entity;)V"))
    private void neotenet$pushSpawn(int p_37714_, Raider p_37715_, BlockPos p_37716_, boolean p_37717_, CallbackInfo ci) {
        p_37715_.pushSpawnCause(org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.RAID);
    }

    // CraftBukkit start - a method to get all raiders
    @Override
    public java.util.Collection<Raider> getRaiders() {
        return this.groupRaiderMap.values().stream().flatMap(Set::stream).collect(java.util.stream.Collectors.toSet());
    }
    // CraftBukkit end

    // CraftBukkit start
    @Override
    public boolean isInProgress() {
        return this.status == Raid.RaidStatus.ONGOING;
    }
    // CraftBukkit end
}
