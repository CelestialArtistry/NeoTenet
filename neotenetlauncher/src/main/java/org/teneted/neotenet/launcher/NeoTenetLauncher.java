package org.teneted.neotenet.launcher;

import cpw.mods.bootstraplauncher.BootstrapLauncher;
import org.teneted.neotenet.launcher.install.Actions;

public class NeoTenetLauncher {

    private static String[] commandLineArgs = new String[0];

    public static void main(String[] args) {
        commandLineArgs = args != null ? args.clone() : new String[0];

        System.setProperty("launcher.args", String.join(",", args));

        // NeoTenet - Prepare Actions

        try {
            Class.forName("org.teneted.neotenet.launcher.install.Actions").getDeclaredMethod("init").invoke(null);
            //Actions.init();

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }


        /*
        if (!InstallationManager.checkAndInstall()) {
            System.err.println("Installation failed!");
            System.exit(1);
        }

        InstallationManager.moveAndRunServerScripts();

         */
    }

    public static String[] getCommandLineArgs() {
        return commandLineArgs.clone();
    }
}