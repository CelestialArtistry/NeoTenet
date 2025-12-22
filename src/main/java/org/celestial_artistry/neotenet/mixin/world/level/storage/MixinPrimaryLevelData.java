package org.celestial_artistry.neotenet.mixin.world.level.storage;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundChangeDifficultyPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.storage.PrimaryLevelData;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.celestial_artistry.neotenet.injection.world.level.storage.InjectionPrimaryLevelData;

@Mixin(PrimaryLevelData.class)
public abstract class MixinPrimaryLevelData implements InjectionPrimaryLevelData {

    @Shadow
    private ServerLevel world;

    @Shadow
    protected Tag pdc;

    @Shadow
    public LevelSettings settings;

    @Shadow
    public Registry<LevelStem> customDimensions;

    @Shadow
    public abstract String getLevelName();

    @Shadow
    private boolean thundering;

    @Shadow
    private boolean raining;

    @Shadow
    public abstract boolean isDifficultyLocked();

    @Redirect(method = "setTagData", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/WorldGenSettings;encode(Lcom/mojang/serialization/DynamicOps;Lnet/minecraft/world/level/levelgen/WorldOptions;Lnet/minecraft/core/RegistryAccess;)Lcom/mojang/serialization/DataResult;"))
    private <T extends Tag> DataResult<T> taiyitist$customDim(DynamicOps<T> ops, WorldOptions options, RegistryAccess registry) {
        return WorldGenSettings.encode(ops, options, new WorldDimensions(this.customDimensions != null ? this.customDimensions : registry.registryOrThrow(Registries.LEVEL_STEM)));
    }

    @Inject(method = "setThundering", cancellable = true, at = @At("HEAD"))
    private void taiyitist$thunder(boolean thunderingIn, CallbackInfo ci) {
        if (this.thundering == thunderingIn) {
            return;
        }

        World world = Bukkit.getWorld(this.getLevelName());
        if (world != null) {
            ThunderChangeEvent event = new ThunderChangeEvent(world, thunderingIn);
            Bukkit.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "setRaining", cancellable = true, at = @At("HEAD"))
    private void taiyitist$storm(boolean isRaining, CallbackInfo ci) {
        if (this.raining == isRaining) {
            return;
        }

        World world = Bukkit.getWorld(this.getLevelName());
        if (world != null) {
            WeatherChangeEvent event = new WeatherChangeEvent(world, isRaining);
            Bukkit.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "setDifficulty", at = @At("RETURN"))
    private void taiyitist$sendDiffChange(Difficulty newDifficulty, CallbackInfo ci) {
        ClientboundChangeDifficultyPacket packet = new ClientboundChangeDifficultyPacket(newDifficulty, this.isDifficultyLocked());
        if (this.world != null) {
            for (Player player : this.world.players()) {
                ((ServerPlayer) player).connection.send(packet);
            }
        }
    }

    @Override
    // CraftBukkit start - Check if the name stored in NBT is the correct one
    public void checkName(String name) {
        if (!this.settings.levelName.equals(name)) {
            this.settings.levelName = name;
        }
    }
    // CraftBukkit end
    @Override
    public void setWorld(ServerLevel world) {
        if (this.world != null) {
            return;
        }
        this.world = world;
        world.getWorld().readBukkitValues(pdc);
        pdc = null;
    }
}
