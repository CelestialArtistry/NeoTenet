package org.teneted.neotenet.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.LevelStem;

public class LevelWrapper {

    public static ResourceKey<LevelStem> levelToLevelStem(ResourceKey<Level> level) {
        if (level == Level.OVERWORLD) {
            return LevelStem.OVERWORLD;
        }else if (level == Level.NETHER) {
            return LevelStem.NETHER;
        } else if (level == Level.END) {
            return LevelStem.END;
        }else {
            return ResourceKey.create(Registries.LEVEL_STEM, level.registry());
        }
    }

    public static ResourceKey<Level> levelStemToLevel(ResourceKey<LevelStem> levelStem) {
        if (levelStem == LevelStem.OVERWORLD) {
            return Level.OVERWORLD;
        } else if (levelStem == LevelStem.NETHER) {
            return Level.NETHER;
        } else if (levelStem == LevelStem.END){
            return Level.END;
        }else {
            return ResourceKey.create(Registries.DIMENSION, levelStem.registry());
        }
    }
}
