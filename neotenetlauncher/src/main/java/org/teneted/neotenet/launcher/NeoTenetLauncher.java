package org.teneted.neotenet.launcher;

import cpw.mods.bootstraplauncher.BootstrapLauncher;
import org.teneted.neotenet.launcher.install.Actions;

public class NeoTenetLauncher {

    private static String[] commandLineArgs = new String[0];

    public static void main(String[] args) {
        commandLineArgs = args != null ? args.clone() : new String[0];

        System.setProperty("launcher.args", String.join(",", commandLineArgs));

        try {
            if (!Actions.ready()) {
                boolean success= Actions.init();
                if (success) {
                    InstallationManager.createVersionFile();
                }
            }
            BootstrapLauncher.main(Actions.parseArgs(commandLineArgs));

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static String[] getCommandLineArgs() {
        return commandLineArgs.clone();
    }
}