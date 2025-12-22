package org.celestial_artistry.neotenet.mixin.server;

import com.google.common.base.Charsets;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Arrays;

import com.mojang.serialization.Dynamic;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.minecraft.SharedConstants;
import net.minecraft.server.Eula;
import net.minecraft.server.Main;
import net.minecraft.server.Services;
import net.minecraft.server.dedicated.DedicatedServerSettings;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess;
import org.bukkit.configuration.file.YamlConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.celestial_artistry.neotenet.NeoTenet;

@Mixin(Main.class)
public abstract class MixinMain {

    @Inject(method = "main", at = @At(value = "INVOKE",
            target = "Ljoptsimple/OptionParser;nonOptions()Ljoptsimple/NonOptionArgumentSpec;",
            shift = At.Shift.AFTER),
            remap = false,
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private static void taiyitist$initMain(String[] p_129699_, CallbackInfo ci, OptionParser optionparser, OptionSpec optionspec, OptionSpec optionspec1, OptionSpec optionspec2, OptionSpec optionspec3, OptionSpec optionspec4, OptionSpec optionspec5, OptionSpec optionspec6, OptionSpec optionspec7, OptionSpec optionspec8, OptionSpec optionspec9, OptionSpec optionspec10, OptionSpec optionspec11, OptionSpec optionspec12, OptionSpec optionspec13, OptionSpec optionspec14) {
        optionparser.acceptsAll(Arrays.asList("b", "bukkit-settings"), "File for bukkit settings")
                .withRequiredArg()
                .ofType(File.class)
                .defaultsTo(new File("bukkit.yml"))
                .describedAs("Yml file");

        optionparser.acceptsAll(Arrays.asList("C", "commands-settings"), "File for command settings")
                .withRequiredArg()
                .ofType(File.class)
                .defaultsTo(new File("commands.yml"))
                .describedAs("Yml file");

        optionparser.acceptsAll(Arrays.asList("P", "plugins"), "Plugin directory to use")
                .withRequiredArg()
                .ofType(File.class)
                .defaultsTo(new File("plugins"))
                .describedAs("Plugin directory");

        // Spigot Start
        optionparser.acceptsAll(Arrays.asList("S", "spigot-settings"), "File for spigot settings")
                .withRequiredArg()
                .ofType(File.class)
                .defaultsTo(new File("spigot.yml"))
                .describedAs("Yml file");
        // Spigot End

        // Spigot Start
        optionparser.acceptsAll(Arrays.asList("B", "banner-settings"), "File for banner settings")
                .withRequiredArg()
                .ofType(File.class)
                .defaultsTo(new File("banner-config", "banner.yml"))
                .describedAs("Yml file");
        // Spigot End
    }

    @Inject(method = "main", at = @At(value = "INVOKE",
            target = "Lorg/slf4j/Logger;info(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"),
            remap = false,
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private static void taiyitist$addYmlInfo(String[] p_129699_, CallbackInfo ci, OptionParser optionparser, OptionSpec optionspec, OptionSpec optionspec1, OptionSpec optionspec2, OptionSpec optionspec3, OptionSpec optionspec4, OptionSpec optionspec5, OptionSpec optionspec6, OptionSpec optionspec7, OptionSpec optionspec8, OptionSpec optionspec9, OptionSpec optionspec10, OptionSpec optionspec11, OptionSpec optionspec12, OptionSpec optionspec13, OptionSpec optionspec14, OptionSpec optionspec15, OptionSpec spawnPosOpt, boolean gametestEnabled, OptionSet optionset, Path path2, Eula eula, Path path, Path path1, DedicatedServerSettings dedicatedserversettings) throws IOException {
        // CraftBukkit start - SPIGOT-5761: Create bukkit.yml and commands.yml if not present
        File configFile = (File) optionset.valueOf("bukkit-settings");
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(configFile);
        configuration.options().copyDefaults(true);
        configuration.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(NeoTenet.class.getClassLoader().getResourceAsStream("configurations/bukkit.yml"), Charsets.UTF_8)));
        configuration.save(configFile);

        File commandFile = (File) optionset.valueOf("commands-settings");
        YamlConfiguration commandsConfiguration = YamlConfiguration.loadConfiguration(commandFile);
        commandsConfiguration.options().copyDefaults(true);
        commandsConfiguration.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(NeoTenet.class.getClassLoader().getResourceAsStream("configurations/commands.yml"), Charsets.UTF_8)));
        commandsConfiguration.save(commandFile);
        // CraftBukkit end
    }

    @Inject(method = "main", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/repository/ServerPacksSource;createPackRepository(Lnet/minecraft/world/level/storage/LevelStorageSource$LevelStorageAccess;)Lnet/minecraft/server/packs/repository/PackRepository;"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void taiyitist$createBukkitDatapack(String[] p_129699_, CallbackInfo ci, OptionParser optionparser, OptionSpec optionspec, OptionSpec optionspec1, OptionSpec optionspec2, OptionSpec optionspec3, OptionSpec optionspec4, OptionSpec optionspec5, OptionSpec optionspec6, OptionSpec optionspec7, OptionSpec optionspec8, OptionSpec optionspec9, OptionSpec optionspec10, OptionSpec optionspec11, OptionSpec optionspec12, OptionSpec optionspec13, OptionSpec optionspec14, OptionSpec optionspec15, OptionSpec spawnPosOpt, boolean gametestEnabled, OptionSet optionset, Path path2, Eula eula, Path path, Path path1, DedicatedServerSettings dedicatedserversettings, File file1, Services services, String s, LevelStorageSource levelstoragesource, LevelStorageAccess levelstoragesource$levelstorageaccess, Dynamic dynamic, Dynamic dynamic1, boolean flag) {
        // CraftBukkit start
        File bukkitDataPackFolder = new File(levelstoragesource.getLevelPath(LevelResource.DATAPACK_DIR.toString()).toFile(), "bukkit");
        if (!bukkitDataPackFolder.exists()) {
            bukkitDataPackFolder.mkdirs();
        }
        File mcMeta = new File(bukkitDataPackFolder, "pack.mcmeta");
        try {
            com.google.common.io.Files.write("{\n"
                    + "    \"pack\": {\n"
                    + "        \"description\": \"Data pack for resources provided by Bukkit plugins\",\n"
                    + "        \"pack_format\": " + SharedConstants.getCurrentVersion().getPackVersion(PackType.SERVER_DATA) + "\n"
                    + "    }\n"
                    + "}\n", mcMeta, Charsets.UTF_8);
        } catch (IOException ex) {
            throw new RuntimeException("Could not initialize Bukkit datapack", ex);
        }
    }

}
