package org.taiyitistmc.launcher;

public class NeoTaiyitistLauncher {

    private static String[] commandLineArgs = new String[0];

    public static void main(String[] args) {
        commandLineArgs = args != null ? args.clone() : new String[0];

        System.setProperty("launcher.args", String.join(",", args));

        if (!InstallationManager.checkAndInstall()) {
            System.err.println("Installation failed!");
            System.exit(1);
        }

        InstallationManager.moveAndRunServerScripts();
    }

    public static String[] getCommandLineArgs() {
        return commandLineArgs.clone();
    }
 }