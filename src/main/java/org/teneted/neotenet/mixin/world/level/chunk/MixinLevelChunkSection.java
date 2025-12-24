package org.teneted.neotenet.mixin.world.level.chunk;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.chunk.PalettedContainerRO;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.teneted.neotenet.injection.world.level.chunk.InjectionLevelChunkSection;

@Mixin(LevelChunkSection.class)
public abstract class MixinLevelChunkSection implements InjectionLevelChunkSection {

    @Final
    @Shadow
    private PalettedContainer<BlockState> states;
    @Shadow
    private PalettedContainerRO<Holder<Biome>> biomes;

    @Override
    public void setBiome(int i, int j, int k, Holder<Biome> biome) {
        ((PalettedContainer<Holder<Biome>>) this.biomes).set(i, j, k, biome);
    }
}
