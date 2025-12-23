package org.celestial_artistry.neotenet.mixin.world.entity;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import org.bukkit.event.entity.EntityUnleashEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Leashable.class)
public interface MixinLeashable {

    @Shadow
    private static <E extends Entity & Leashable> void dropLeash(E p_352163_, boolean p_352286_, boolean p_352272_) {

    }

    @Inject(method = "writeLeashData", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/decoration/LeashFenceKnotEntity;getPos()Lnet/minecraft/core/BlockPos;"), cancellable = true)
    private void neotenet$dontSave(CompoundTag p_352349_, Leashable.LeashData p_352363_, CallbackInfo ci, @Local(ordinal = 0) LeashFenceKnotEntity leashfenceknotentity) {
        // CraftBukkit start - SPIGOT-7487: Don't save (and possible drop) leash, when the holder was removed by a plugin
        if (leashfenceknotentity != null && leashfenceknotentity.pluginRemoved) {
            ci.cancel();
            return;
        }
        // CraftBukkit end
    }

    @Inject(method = "restoreLeashFromSave", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;spawnAtLocation(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/entity/item/ItemEntity;"))
    private static <E extends Entity & Leashable> void neotenet$forceTick0(E p_352354_, Leashable.LeashData p_352106_, CallbackInfo ci) {
        p_352354_.forceDrops = true; // CraftBukkit
    }

    @Inject(method = "restoreLeashFromSave", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;spawnAtLocation(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/entity/item/ItemEntity;", shift = At.Shift.AFTER))
    private static <E extends Entity & Leashable> void neotenet$forceTick1(E p_352354_, Leashable.LeashData p_352106_, CallbackInfo ci) {
        p_352354_.forceDrops = false; // CraftBukkit
    }

    @Inject(method = "dropLeash(Lnet/minecraft/world/entity/Entity;ZZ)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;spawnAtLocation(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/entity/item/ItemEntity;"))
    private static <E extends Entity & Leashable> void neotenet$forceTick2(E p_352163_, boolean p_352286_, boolean p_352272_, CallbackInfo ci) {
        p_352163_.forceDrops = true; // CraftBukkit
    }

    @Inject(method = "dropLeash(Lnet/minecraft/world/entity/Entity;ZZ)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;spawnAtLocation(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/entity/item/ItemEntity;", shift = At.Shift.AFTER))
    private static <E extends Entity & Leashable> void neotenet$forceTick3(E p_352163_, boolean p_352286_, boolean p_352272_, CallbackInfo ci) {
        p_352163_.forceDrops = false; // CraftBukkit
    }

    @Inject(method = "tickLeash", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Leashable;dropLeash(Lnet/minecraft/world/entity/Entity;ZZ)V"))
    private static <E extends Entity & Leashable> void neotenet$leashEvent(E p_352082_, CallbackInfo ci) {
        p_352082_.level().getCraftServer().getPluginManager().callEvent(new EntityUnleashEvent(p_352082_.getBukkitEntity(), (!p_352082_.isAlive()) ? EntityUnleashEvent.UnleashReason.PLAYER_UNLEASH : EntityUnleashEvent.UnleashReason.HOLDER_GONE)); // CraftBukkit
        dropLeash(p_352082_, true, !p_352082_.pluginRemoved); // CraftBukkit - SPIGOT-7487: Don't drop leash, when the holder was removed by a plugin
    }

    @Inject(method = "leashTooFarBehaviour", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Leashable;dropLeash(ZZ)V"))
    private void neotenet$leashEvent2(CallbackInfo ci) {
        // CraftBukkit start
        if (((Leashable) (Object) this) instanceof Entity entity) {
            entity.level().getCraftServer().getPluginManager().callEvent(new EntityUnleashEvent(entity.getBukkitEntity(), EntityUnleashEvent.UnleashReason.DISTANCE));
        }
        // CraftBukkit end
    }
}
