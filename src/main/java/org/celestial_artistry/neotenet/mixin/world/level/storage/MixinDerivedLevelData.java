package org.celestial_artistry.neotenet.mixin.world.level.storage;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.WorldData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.celestial_artistry.neotenet.injection.world.level.storage.InjectionDerivedLevelData;

@Mixin(DerivedLevelData.class)
public class MixinDerivedLevelData implements InjectionDerivedLevelData {

    @Shadow
    private ResourceKey<LevelStem> typeKey;

    @Shadow
    @Final
    public ServerLevelData wrapped;

    @Override
    public void setDimType(ResourceKey<LevelStem> typeKey) {
        this.typeKey = typeKey;
    }

    @Redirect(method = "getLevelName", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/WorldData;getLevelName()Ljava/lang/String;"))
    private String neotenet$resetLevelName(WorldData instance) {
        if (typeKey == null || typeKey == LevelStem.OVERWORLD) {
            return this.wrapped.getLevelName();
        } else {
            String worldName = this.wrapped.getLevelName() + "/";
            String suffix;
            if (typeKey == LevelStem.END) {
                suffix = "DIM1";
            } else if (typeKey == LevelStem.NETHER) {
                suffix = "DIM-1";
            } else {
                suffix = typeKey.location().getNamespace() + "/" + typeKey.location().getPath();
            }
            return worldName + suffix;
        }
    }
}
