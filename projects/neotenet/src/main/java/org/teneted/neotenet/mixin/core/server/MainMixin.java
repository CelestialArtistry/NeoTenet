package org.teneted.neotenet.mixin.core.server;

import com.google.common.base.Charsets;
import com.llamalad7.mixinextras.sugar.Local;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.minecraft.SharedConstants;
import net.minecraft.server.Main;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.bukkit.configuration.file.YamlConfiguration;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

@Mixin(Main.class)
public class MainMixin {

    @Inject(method = "main", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;info(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"))
    private static void neotenet$handleBukkitConfig(String[] args, CallbackInfo ci, @Local OptionParser parser, @Local OptionSet optionset) throws IOException {
        // CraftBukkit start - SPIGOT-5761: Create bukkit.yml and commands.yml if not present
        File configFile = (File) optionset.valueOf("bukkit-settings");
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(configFile);
        configuration.options().copyDefaults(true);
        configuration.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(Main.class.getClassLoader().getResourceAsStream("configurations/bukkit.yml"), Charsets.UTF_8)));
        configuration.save(configFile);

        File commandFile = (File) optionset.valueOf("commands-settings");
        YamlConfiguration commandsConfiguration = YamlConfiguration.loadConfiguration(commandFile);
        commandsConfiguration.options().copyDefaults(true);
        commandsConfiguration.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(Main.class.getClassLoader().getResourceAsStream("configurations/commands.yml"), Charsets.UTF_8)));
        commandsConfiguration.save(commandFile);
        // CraftBukkit end
    }

    @Inject(method = "main", at = @At(value = "CONSTANT", args = "classValue=net/minecraft/server/WorldStem"))
    private static void neotenet$putBukkitDatapacks(String[] args, CallbackInfo ci, @Local LevelStorageSource.LevelStorageAccess access) {
        // CraftBukkit start
        File bukkitDataPackFolder = new File(access.getLevelPath(LevelResource.DATAPACK_DIR).toFile(), "bukkit");
        if (!bukkitDataPackFolder.exists()) {
            bukkitDataPackFolder.mkdirs();
        }
        File mcMeta = new File(bukkitDataPackFolder, "pack.mcmeta");
        try {
            com.google.common.io.Files.write("{\n"
                    + "    \"pack\": {\n"
                    + "        \"description\": \"Data pack for resources provided by Bukkit plugins\",\n"
                    + "        \"min_format\": " + SharedConstants.getCurrentVersion().packVersion(PackType.SERVER_DATA) + ",\n"
                    + "        \"max_format\": " + SharedConstants.getCurrentVersion().packVersion(PackType.SERVER_DATA) + "\n"
                    + "    }\n"
                    + "}\n", mcMeta, com.google.common.base.Charsets.UTF_8);
        } catch (java.io.IOException ex) {
            throw new RuntimeException("Could not initialize Bukkit datapack", ex);
        }
    }

    @Redirect(method = "forceUpgrade", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;info(Ljava/lang/String;)V"))
    private static void neotenet$addUpgradeMessage(Logger instance, String s, @Local(argsOnly = true) LevelStorageSource.LevelStorageAccess storageSource) {
        instance.info("Forcing world upgrade! {}", storageSource.getLevelId()); // CraftBukkit
    }
}
