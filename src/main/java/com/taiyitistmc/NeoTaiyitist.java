package com.taiyitistmc;

import net.neoforged.neoforge.internal.versions.neoforge.NeoForgeVersion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.ZoneId;

public class NeoTaiyitist {

    public static final String MOD_ID = "neotaiyitist";
    public static final Logger LOGGER = LogManager.getLogger();
    public static final float javaVersion = Float.parseFloat(System.getProperty("java.class.version"));

    public static void run() throws Exception {
        LOGGER.info(" _____       ___   _  __    __  _   _____   _   _____   _____  ");
        LOGGER.info("|_   _|     /   | | | \\ \\  / / | | |_   _| | | /  ___/ |_   _| ");
        LOGGER.info("  | |      / /| | | |  \\ \\/ /  | |   | |   | | | |___    | |   ");
        LOGGER.info("  | |     / /_| | | |   \\  /   | |   | |   | | \\___  \\   | |   ");
        LOGGER.info("  | |    / ___  | | |   / /    | |   | |   | |  ___| |   | |   ");
        LOGGER.info("  |_|   /_/   |_| |_|  /_/     |_|   |_|   |_| /_____/   |_|   ");
        LOGGER.info("{} {}, Java {}", "Welecom to NeoTaiyitist for neoforge", NeoForgeVersion.getVersion(), javaVersion);
        ZoneId zoneId = ZoneId.of("Asia/Shanghai");
        if(zoneId.getId().contains("China")) {
            System.out.printf("官方交流QQ群: 211128424%n");
            System.out.printf("如果控制台出现中文乱码请添加启动参数: -Dfile.encoding=GBK%n");
        }
    }
}
