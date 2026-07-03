package org.teneted.neotenet.launcher;

import java.io.*;
import java.nio.file.*;

public class InstallationManager {

    public static boolean checkAndInstall() {
        try {
            if (isAlreadyInstalled()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean isAlreadyInstalled() {
        String[] args = getCommandLineArgs();
        for (String arg : args) {
            if ("-dev".equals(arg)) {
                System.out.println("Dev mode enabled, forcing reinstallation");
                return false;
            }
        }

        try {
            Path versionFile = Paths.get("libraries/org/teneted/neotenet/version.txt");
            if (Files.exists(versionFile)) {
                String existingVersion = new String(Files.readAllBytes(versionFile)).trim();
                if (!getImplementationVersion().equals(existingVersion)) {
                    System.out.println("Version mismatch. Existing: " + existingVersion + ", Expected: " + getImplementationVersion());
                    return false;
                }
            } else {
                System.out.println("Version file not found: " + versionFile);
                return false;
            }

            System.out.println("Installation already exists");
            return true;
        } catch (Exception e) {
            System.err.println("Error checking installation status: " + e.getMessage());
            return false;
        }
    }

    private static String getImplementationVersion() {
        String version = InstallationManager.class.getPackage().getImplementationVersion();
        if (version != null && !version.isEmpty()) {
            return version;
        }
        return null;
    }

    public static void createVersionFile() {
        try {
            Path versionDir = Paths.get("libraries/org/teneted/neotenet");
            Files.createDirectories(versionDir);
            
            Path versionFile = versionDir.resolve("version.txt");
            String version = getImplementationVersion() != null ? getImplementationVersion() : "unknown";
            Files.write(versionFile, version.getBytes());
            
            System.out.println("Created version file at: " + versionFile.toAbsolutePath());
        } catch (Exception e) {
            System.err.println("Failed to create version file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String[] getCommandLineArgs() {
        try {
            return NeoTenetLauncher.getCommandLineArgs();
        } catch (Exception e) {
            String argsStr = System.getProperty("launcher.args");
            if (argsStr != null && !argsStr.isEmpty()) {
                return argsStr.split(",");
            }
            return new String[0];
        }
    }
}