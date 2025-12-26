package org.teneted.neotenet.config;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class NeoTenetConfigUtil {

    public static final File neotenetYml = new File("neotenet.yml");
    public static final YamlConfiguration yml = YamlConfiguration.loadConfiguration(neotenetYml);

    public static void copyNeotenetConfig() {
        try {
            if (!neotenetYml.exists()) {
                neotenetYml.createNewFile();
            }
        } catch (Exception e) {
            System.out.println("File init exception!");
        }
    }

    public static boolean aBoolean(String key, boolean defaultReturn) {
        return yml.getBoolean(key, defaultReturn);
    }

    public static void save() {
        try {
            yml.save(neotenetYml);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String lang() {
        String lang = "localization.locale";
        if (yml.get(lang) == null) {
            yml.set(lang, Locale.getDefault().toString());
            save();
        }
        return yml.getString(lang, Locale.getDefault().toString());
    }

    public static boolean showLogo() {
        String key = "neotenet.show_logo";
        if (yml.get(key) == null) {
            yml.set(key, true);
            save();
        }
        return yml.getBoolean(key, true);
    }
}
