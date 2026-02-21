package org.teneted.neotenet.mixin.world.level.chunk;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.FeatureSorter;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.teneted.neotenet.injection.world.level.chunk.InjectionChunkGenerator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Mixin(ChunkGenerator.class)
public abstract class MixinChunkGenerator implements InjectionChunkGenerator {

    @Shadow
    @Final
    protected BiomeSource biomeSource;

    @Shadow
    @Final
    private Supplier<List<FeatureSorter.StepFeatureData>> featuresPerStep;

    @Shadow
    protected static BoundingBox getWritableArea(ChunkAccess p_187718_) {
        return null;
    }

    @Shadow
    @Final
    private Function<Holder<Biome>, BiomeGenerationSettings> generationSettingsGetter;

    @Override
    public void addVanillaDecorations(WorldGenLevel p_223087_, ChunkAccess p_223088_, StructureManager p_223089_) {
        ChunkPos chunkpos = p_223088_.getPos();
        if (!SharedConstants.debugVoidTerrain(chunkpos)) {
            SectionPos sectionpos = SectionPos.of(chunkpos, p_223087_.getMinSection());
            BlockPos blockpos = sectionpos.origin();
            Registry<Structure> registry = p_223087_.registryAccess().registryOrThrow(Registries.STRUCTURE);
            Map<Integer, List<Structure>> map = registry.stream().collect(Collectors.groupingBy(p_223103_ -> p_223103_.step().ordinal()));
            List<FeatureSorter.StepFeatureData> list = this.featuresPerStep.get();
            WorldgenRandom worldgenrandom = new WorldgenRandom(new XoroshiroRandomSource(RandomSupport.generateUniqueSeed()));
            long i = worldgenrandom.setDecorationSeed(p_223087_.getSeed(), blockpos.getX(), blockpos.getZ());
            Set<Holder<Biome>> set = new ObjectArraySet<>();
            ChunkPos.rangeClosed(sectionpos.chunk(), 1).forEach(p_223093_ -> {
                ChunkAccess chunkaccess = p_223087_.getChunk(p_223093_.x, p_223093_.z);

                for (LevelChunkSection levelchunksection : chunkaccess.getSections()) {
                    levelchunksection.getBiomes().getAll(set::add);
                }
            });
            set.retainAll(this.biomeSource.possibleBiomes());
            int j = list.size();

            try {
                Registry<PlacedFeature> registry1 = p_223087_.registryAccess().registryOrThrow(Registries.PLACED_FEATURE);
                int i1 = Math.max(GenerationStep.Decoration.values().length, j);

                for (int k = 0; k < i1; k++) {
                    int l = 0;
                    if (p_223089_.shouldGenerateStructures()) {
                        for (Structure structure : map.getOrDefault(k, Collections.emptyList())) {
                            worldgenrandom.setFeatureSeed(i, l, k);
                            Supplier<String> supplier = () -> registry.getResourceKey(structure).map(Object::toString).orElseGet(structure::toString);

                            try {
                                p_223087_.setCurrentlyGenerating(supplier);
                                p_223089_.startsForStructure(sectionpos, structure)
                                        .forEach(
                                                p_223086_ -> p_223086_.placeInChunk(p_223087_, p_223089_, ((ChunkGenerator) (Object) this), worldgenrandom, getWritableArea(p_223088_), chunkpos)
                                        );
                            } catch (Exception exception) {
                                CrashReport crashreport1 = CrashReport.forThrowable(exception, "Feature placement");
                                crashreport1.addCategory("Feature").setDetail("Description", supplier::get);
                                throw new ReportedException(crashreport1);
                            }

                            l++;
                        }
                    }

                    if (k < j) {
                        IntSet intset = new IntArraySet();

                        for (Holder<Biome> holder : set) {
                            List<HolderSet<PlacedFeature>> list1 = this.generationSettingsGetter.apply(holder).features();
                            if (k < list1.size()) {
                                HolderSet<PlacedFeature> holderset = list1.get(k);
                                FeatureSorter.StepFeatureData featuresorter$stepfeaturedata1 = list.get(k);
                                holderset.stream()
                                        .map(Holder::value)
                                        .forEach(p_223174_ -> intset.add(featuresorter$stepfeaturedata1.indexMapping().applyAsInt(p_223174_)));
                            }
                        }

                        int j1 = intset.size();
                        int[] aint = intset.toIntArray();
                        Arrays.sort(aint);
                        FeatureSorter.StepFeatureData featuresorter$stepfeaturedata = list.get(k);

                        for (int k1 = 0; k1 < j1; k1++) {
                            int l1 = aint[k1];
                            PlacedFeature placedfeature = featuresorter$stepfeaturedata.features().get(l1);
                            Supplier<String> supplier1 = () -> registry1.getResourceKey(placedfeature).map(Object::toString).orElseGet(placedfeature::toString);
                            worldgenrandom.setFeatureSeed(i, l1, k);

                            try {
                                p_223087_.setCurrentlyGenerating(supplier1);
                                placedfeature.placeWithBiomeCheck(p_223087_, ((ChunkGenerator) (Object) this), worldgenrandom, blockpos);
                            } catch (Exception exception1) {
                                CrashReport crashreport2 = CrashReport.forThrowable(exception1, "Feature placement");
                                crashreport2.addCategory("Feature").setDetail("Description", supplier1::get);
                                throw new ReportedException(crashreport2);
                            }
                        }
                    }
                }

                p_223087_.setCurrentlyGenerating(null);
            } catch (Exception exception2) {
                CrashReport crashreport = CrashReport.forThrowable(exception2, "Biome decoration");
                crashreport.addCategory("Generation").setDetail("CenterX", chunkpos.x).setDetail("CenterZ", chunkpos.z).setDetail("Decoration Seed", i);
                throw new ReportedException(crashreport);
            }
        }
    }

    // CraftBukkit start

    @Inject(method = "applyBiomeDecoration", at = @At("RETURN"))
    private void neotenet$checkVanilla(WorldGenLevel generatoraccessseed, ChunkAccess ichunkaccess, StructureManager p_223089_, CallbackInfo ci) {
        org.bukkit.World world = generatoraccessseed.getMinecraftWorld().getWorld();
        // only call when a populator is present (prevents unnecessary entity conversion)
        if (!world.getPopulators().isEmpty()) {
            org.bukkit.craftbukkit.generator.CraftLimitedRegion limitedRegion = new org.bukkit.craftbukkit.generator.CraftLimitedRegion(generatoraccessseed, ichunkaccess.getPos());
            int x = ichunkaccess.getPos().x;
            int z = ichunkaccess.getPos().z;
            for (org.bukkit.generator.BlockPopulator populator : world.getPopulators()) {
                WorldgenRandom seededrandom = new WorldgenRandom(new net.minecraft.world.level.levelgen.LegacyRandomSource(generatoraccessseed.getSeed()));
                seededrandom.setDecorationSeed(generatoraccessseed.getSeed(), x, z);
                populator.populate(world, new org.bukkit.craftbukkit.util.RandomSourceWrapper.RandomWrapper(seededrandom), x, z, limitedRegion);
            }
            limitedRegion.saveEntities();
            limitedRegion.breakLink();
        }
    }

    @Override
    public void applyBiomeDecoration(WorldGenLevel generatoraccessseed, ChunkAccess ichunkaccess, StructureManager structuremanager, boolean vanilla) {
        if (vanilla) {
            addVanillaDecorations(generatoraccessseed, ichunkaccess, structuremanager);
        }

        org.bukkit.World world = generatoraccessseed.getMinecraftWorld().getWorld();
        // only call when a populator is present (prevents unnecessary entity conversion)
        if (!world.getPopulators().isEmpty()) {
            org.bukkit.craftbukkit.generator.CraftLimitedRegion limitedRegion = new org.bukkit.craftbukkit.generator.CraftLimitedRegion(generatoraccessseed, ichunkaccess.getPos());
            int x = ichunkaccess.getPos().x;
            int z = ichunkaccess.getPos().z;
            for (org.bukkit.generator.BlockPopulator populator : world.getPopulators()) {
                WorldgenRandom seededrandom = new WorldgenRandom(new net.minecraft.world.level.levelgen.LegacyRandomSource(generatoraccessseed.getSeed()));
                seededrandom.setDecorationSeed(generatoraccessseed.getSeed(), x, z);
                populator.populate(world, new org.bukkit.craftbukkit.util.RandomSourceWrapper.RandomWrapper(seededrandom), x, z, limitedRegion);
            }
            limitedRegion.saveEntities();
            limitedRegion.breakLink();
        }
    }
    // CraftBukkit end

    @Inject(method = "tryGenerateStructure", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/StructureManager;setStartForStructure(Lnet/minecraft/core/SectionPos;Lnet/minecraft/world/level/levelgen/structure/Structure;Lnet/minecraft/world/level/levelgen/structure/StructureStart;Lnet/minecraft/world/level/chunk/StructureAccess;)V"), cancellable = true)
    private void neotenet$callAsyncStructureSpawnEvent(StructureSet.StructureSelectionEntry p_223105_, StructureManager p_223106_, RegistryAccess p_223107_, RandomState p_223108_, StructureTemplateManager p_223109_, long p_223110_, ChunkAccess p_223111_, ChunkPos p_223112_, SectionPos p_223113_, CallbackInfoReturnable<Boolean> cir, @Local StructureStart structurestart, @Local Structure structure) {
        // CraftBukkit start
        BoundingBox box = structurestart.getBoundingBox();
        org.bukkit.event.world.AsyncStructureSpawnEvent event = new org.bukkit.event.world.AsyncStructureSpawnEvent(p_223106_.level.getMinecraftWorld().getWorld(), org.bukkit.craftbukkit.generator.structure.CraftStructure.minecraftToBukkit(structure), new org.bukkit.util.BoundingBox(box.minX(), box.minY(), box.minZ(), box.maxX(), box.maxY(), box.maxZ()), p_223112_.x, p_223112_.z);
        org.bukkit.Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            cir.setReturnValue(true);
        }
        // CraftBukkit end
    }
}
