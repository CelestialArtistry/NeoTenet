package org.celestial_artistry.neotenet.mixin.world.level.saveddata.maps;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.map.CraftMapView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MapItemSavedData.class)
public class MixinMapItemSavedData {

    @Shadow
    public ResourceKey<Level> dimension;

    @Mutable
    @Shadow
    @Final
    public CraftMapView mapView;

    @Shadow
    private CraftServer server;

    @Shadow
    public UUID uniqueId;

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @Redirect(method = "load", at = @At(value = "INVOKE", target = "Ljava/util/Optional;orElseThrow(Ljava/util/function/Supplier;)Ljava/lang/Object;"))
    private static Object neotenet$customDimension(Optional<ResourceKey<Level>> optional, Supplier<?> exceptionSupplier, CompoundTag nbt) {
        return optional.orElseGet(() -> {
            long least = nbt.getLong("UUIDLeast");
            long most = nbt.getLong("UUIDMost");
            if (least != 0L && most != 0L) {
                UUID uniqueId = new UUID(most, least);
                CraftWorld world = (CraftWorld) Bukkit.getWorld(uniqueId);
                if (world != null) {
                    return world.getHandle().dimension();
                }
            }
            throw new IllegalArgumentException("Invalid map dimension: " + nbt.get("dimension"));
        });
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    public void neotenet$init(int i, int j, byte b, boolean bl, boolean bl2, boolean bl3, ResourceKey resourceKey, CallbackInfo ci) {
        this.mapView = new CraftMapView((MapItemSavedData) (Object) this);
        this.server = (CraftServer) Bukkit.getServer();
    }

    @Inject(method = "save", at = @At("HEAD"))
    public void neotenet$storeDimension(CompoundTag compoundTag, HolderLookup.Provider provider, CallbackInfoReturnable<CompoundTag> cir) {
        if (this.uniqueId == null) {
            for (org.bukkit.World world : this.server.getWorlds()) {
                CraftWorld cWorld = (CraftWorld) world;
                if (cWorld.getHandle().dimension() != this.dimension) continue;
                this.uniqueId = cWorld.getUID();
                break;
            }
        }
        if (this.uniqueId != null) {
            compoundTag.putLong("UUIDLeast", this.uniqueId.getLeastSignificantBits());
            compoundTag.putLong("UUIDMost", this.uniqueId.getMostSignificantBits());
        }
    }
}
