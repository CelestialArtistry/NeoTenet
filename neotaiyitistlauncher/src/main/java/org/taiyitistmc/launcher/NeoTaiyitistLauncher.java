package org.taiyitistmc.launcher;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class NeoTaiyitistLauncher {

    private static String[] commandLineArgs = new String[0];

    public static void main(String[] args) throws IOException {
        commandLineArgs = args != null ? args.clone() : new String[0];

        System.setProperty("launcher.args", String.join(",", args));

        if (!InstallationManager.checkAndInstall()) {
            System.err.println("Installation failed!");
            System.exit(1);
        }

        InstallationManager.moveAndRunServerScripts();
        Path sourceRunBat = Paths.get("run.bat");
        Path sourceRunSh = Paths.get("run.sh");
        if (Files.exists(sourceRunBat)) {
            Files.deleteIfExists(sourceRunBat);
        }
        if (Files.exists(sourceRunSh)) {
            Files.deleteIfExists(sourceRunSh);
        }
    }

    public static String[] getCommandLineArgs() {
        return commandLineArgs.clone();
    }
 }