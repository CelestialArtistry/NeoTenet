package org.teneted.neotenet.neoforge;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.HangingSignBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.TrappedChestBlockEntity;
import net.minecraft.world.level.dimension.LevelStem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.craftbukkit.block.CraftBlockStates;
import org.bukkit.craftbukkit.block.CraftChest;
import org.bukkit.craftbukkit.block.CraftHangingSign;
import org.bukkit.craftbukkit.block.CraftSign;
import org.bukkit.craftbukkit.inventory.CraftRecipe;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.entity.Villager;
import org.teneted.neotenet.NeoTenet;
import org.teneted.neotenet.bukkit.MaterialHelper;
import org.teneted.neotenet.util.EnumHelper;

@SuppressWarnings({"ConstantConditions", "deprecation"})
public class BukkitRegistry {

    public static final BiMap<ResourceKey<LevelStem>, World.Environment> environment =
            HashBiMap.create(ImmutableMap.<ResourceKey<LevelStem>, World.Environment>builder()
                    .put(LevelStem.OVERWORLD, World.Environment.NORMAL)
                    .put(LevelStem.NETHER, World.Environment.NETHER)
                    .put(LevelStem.END, World.Environment.THE_END)
                    .build());

    public static BiMap<World.Environment, ResourceKey<LevelStem>> environment0 =
            HashBiMap.create(ImmutableMap.<World.Environment, ResourceKey<LevelStem>>builder()
                    .put(World.Environment.NORMAL, LevelStem.OVERWORLD)
                    .put(World.Environment.NETHER, LevelStem.NETHER)
                    .put(World.Environment.THE_END, LevelStem.END)
                    .build());

    public static Map<ResourceKey<Registry<VillagerProfession>>, Villager.Profession> profession = new HashMap<>();

    public static void registerAll(DedicatedServer console) {
        loadItems();
        loadBlocks();
        loadCookingBookCategory();
    }

    public static void loadItems() {
        var registry = BuiltInRegistries.ITEM;
        var newTypes = new ArrayList<Material>();
        for (Item item : registry) {
            ResourceLocation resourceLocation = registry.getKey(item);
            if (isMods(resourceLocation)) {
                // inject item materials into Bukkit for Fabric
                String materialName = normalizeName(resourceLocation.toString());
                int id = Item.getId(item);
                Material material = MaterialHelper.addMaterial(materialName, id, item.getDefaultMaxStackSize(), false, true, resourceLocation);

                newTypes.add(material);

                CraftMagicNumbers.ITEM_MATERIAL.put(item, material);
                CraftMagicNumbers.MATERIAL_ITEM.put(material, item);
                NeoTenet.LOGGER.debug("Save-ITEM: " + material.name() + " - " + material.key);
            }
        }
        NeoTenet.LOGGER.info("Registered {} new items", newTypes.size());
    }

    public static void loadBlocks() {
        var registry = BuiltInRegistries.BLOCK;
        var newTypes = new ArrayList<Material>();

        for (Block block : registry) {
            ResourceLocation resourceLocation = registry.getKey(block);
            if (isMods(resourceLocation)) {
                // inject block materials into Bukkit for Fabric
                String materialName = normalizeName(resourceLocation.toString());
                int id = Item.getId(block.asItem());
                Item item = Item.byId(id);
                Material material = MaterialHelper.addMaterial(materialName, id, item.getDefaultMaxStackSize(), true, false, resourceLocation);
                newTypes.add(material);

                if (material != null) {
                    CraftMagicNumbers.BLOCK_MATERIAL.put(block, material);
                    CraftMagicNumbers.MATERIAL_BLOCK.put(material, block);
                    if (block.defaultBlockState().is(BlockTags.SIGNS)) {
                        CraftBlockStates.register(material, CraftSign.class, CraftSign::new, SignBlockEntity::new);
                    } else if (block.defaultBlockState().is(BlockTags.ALL_HANGING_SIGNS)) {
                        CraftBlockStates.register(material, CraftHangingSign.class, CraftHangingSign::new, HangingSignBlockEntity::new);
                    } else if (block instanceof SignBlock signBlock) {
                        BlockEntity blockEntity = signBlock.newBlockEntity(BlockPos.ZERO, block.defaultBlockState());
                        if (blockEntity instanceof HangingSignBlockEntity) {
                            CraftBlockStates.register(material, CraftHangingSign.class, CraftHangingSign::new, HangingSignBlockEntity::new);
                        } else if (blockEntity instanceof SignBlockEntity) {
                            CraftBlockStates.register(material, CraftSign.class, CraftSign::new, SignBlockEntity::new);
                        }
                    } else if (block instanceof ChestBlock chestBlock) {
                        BlockEntity blockEntity = chestBlock.newBlockEntity(BlockPos.ZERO, block.defaultBlockState());
                        if (blockEntity instanceof TrappedChestBlockEntity) {
                            CraftBlockStates.register(material, CraftChest.class, CraftChest::new, TrappedChestBlockEntity::new);
                        } else if (blockEntity instanceof ChestBlockEntity) {
                            CraftBlockStates.register(material, CraftChest.class, CraftChest::new, ChestBlockEntity::new);
                        }
                    }
                    NeoTenet.LOGGER.debug("Registered {0} as block {1}" + material.name() + " - " + material.key);
                }
            }
        }
        NeoTenet.LOGGER.info("Registered {} new blocks", newTypes.size());
    }


    private static void loadCookingBookCategory() {
        var newTypes = new ArrayList<org.bukkit.inventory.recipe.CookingBookCategory>();
        for (CookingBookCategory category : CookingBookCategory.values()) {
            try {
                CraftRecipe.getCategory(category);
            } catch (Exception e) {
                var name = category.name();
                var bukkit = EnumHelper.addEnum(org.bukkit.inventory.recipe.CookingBookCategory.class, name);
                newTypes.add(bukkit);
                NeoTenet.LOGGER.debug("Registered {} as cooking category {}", name, bukkit);
            }
        }
        NeoTenet.LOGGER.info("Registered {} new cooking book categories", newTypes.size());
    }

    public static void registerEnvironments(Registry<LevelStem> registry) {
        int i = World.Environment.values().length;
        List<World.Environment> newTypes = new ArrayList<>();
        for (Map.Entry<ResourceKey<LevelStem>, LevelStem> entry : registry.entrySet()) {
            ResourceKey<LevelStem> key = entry.getKey();
            World.Environment environment1 = environment.get(key);
            if (environment1 == null) {
                String name = normalizeName(key.location().toString());
                int id = i - 1;
                environment1 = EnumHelper.addEnum(World.Environment.class, name, List.of(Integer.TYPE), List.of(id));
                environment.put(key, environment1);
                environment0.put(environment1, key);
                NeoTenet.LOGGER.debug("Registered mod DimensionType as environment {}", environment1);
                i++;
            }
        }
    }

    public static String normalizeName(String name) {
        return name.replace(':', '_')
                .replaceAll("\\s+", "_")
                .replaceAll("\\W", "")
                .toUpperCase(Locale.ENGLISH);
    }

    public static boolean isMods(ResourceLocation resourceLocation) {
        return !resourceLocation.getNamespace().equals(NamespacedKey.MINECRAFT);
    }

    public static boolean isMods(NamespacedKey namespacedkey) {
        return !namespacedkey.getNamespace().equals(NamespacedKey.MINECRAFT);
    }
}