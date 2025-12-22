package org.taiyitistmc;

import com.github.lalyos.jfiglet.FigletFont;
import net.neoforged.neoforge.internal.versions.neoforge.NeoForgeVersion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.time.ZoneId;

public class NeoTaiyitist {

    public static final String MOD_ID = "neotaiyitist";
    public static final Logger LOGGER = LogManager.getLogger();
    public static final float javaVersion = Float.parseFloat(System.getProperty("java.class.version"));

    // ANSI color codes
    private static final String CYAN = "\u001B[36m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RESET = "\u001B[0m";

    public static void run() throws Exception {
        InputStream fontStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("larry3d.flf");
        String banner = FigletFont.convertOneLine(fontStream, "NeoTaiyitist");
        for (String line : banner.split("\n")) {
            System.out.println(CYAN + line + RESET);
        }

        System.out.println(YELLOW + "Welcome to NeoTaiyitist for NeoForge " + NeoForgeVersion.getVersion() + ", Java " + javaVersion + RESET);

        ZoneId zoneId = ZoneId.of("Asia/Shanghai");
        if (zoneId.getId().contains("China")) {
            System.out.println("官方交流QQ群: 211128424");
            System.out.println("如果控制台出现中文乱码请添加启动参数: -Dfile.encoding=GBK%n");
        }
    }
}
