package org.celestial_artistry.neotenet.launcher;

import java.io.*;
import java.nio.file.*;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InstallationManager {

    private static String NEOFORGE_VERSION = "21.1.217";

    public static boolean checkAndInstall() {
        try {
            if (isAlreadyInstalled()) {
                return true;
            }

            Path installerPath = extractEmbeddedInstaller();
            if (installerPath != null) {
                boolean success = runInstaller(installerPath);
                try {
                    Files.deleteIfExists(installerPath);
                    Files.deleteIfExists(installerPath.getParent());
                } catch (Exception ignored) {}

                return success;
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
            Path universalJar = Paths.get("libraries/net/neoforged/neoforge/" + NEOFORGE_VERSION + "/neoforge-" + NEOFORGE_VERSION + "-universal.jar");

            String sysType = File.pathSeparatorChar == ';' ? "win" : "unix";
            Path argsFile = Paths.get("libraries/net/neoforged/neoforge/" + NEOFORGE_VERSION + "/" + sysType + "_args.txt");

            if (!Files.exists(universalJar)) {
                System.out.println("Universal jar not found: " + universalJar);
                return false;
            }

            if (!Files.exists(argsFile)) {
                System.out.println("Args file not found: " + argsFile);
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

    private static Path extractEmbeddedInstaller() throws Exception {
        String jarPath = InstallationManager.class.getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .toURI()
                .getPath();

        try (JarFile jarFile = new JarFile(jarPath)) {
            Enumeration<JarEntry> entries = jarFile.entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.getName().startsWith("META-INF/installer/") &&
                        entry.getName().endsWith("-installer.jar")) {

                    Path tempDir = Files.createTempDirectory("neotaiyitist-installer-");
                    Path installerPath = tempDir.resolve("installer.jar");

                    try (InputStream is = jarFile.getInputStream(entry);
                         FileOutputStream fos = new FileOutputStream(installerPath.toFile())) {

                        byte[] buffer = new byte[8192];
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1) {
                            fos.write(buffer, 0, bytesRead);
                        }
                    }

                    return installerPath;
                }
            }
        }
        return null;
    }

    private static boolean runInstaller(Path installerPath) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    Paths.get(System.getProperty("java.home"), "bin", "java").toString(),
                    "-jar",
                    installerPath.toString(),
                    "--installServer",
                    ".",
                    "--debug"
            );
            pb.inheritIO();
            Process process = pb.start();
            return process.waitFor() == 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void moveAndRunServerScripts() {
        try {
            Path targetDir = Paths.get("libraries/org/taiyitistmc/launcher/script");
            Files.createDirectories(targetDir);

            Path targetRunBat = targetDir.resolve("run.bat");
            Path targetRunSh = targetDir.resolve("run.sh");

            boolean scriptsExist = Files.exists(targetRunBat) && Files.exists(targetRunSh);

            if (!scriptsExist) {
                Path sourceRunBat = Paths.get("run.bat");
                if (Files.exists(sourceRunBat)) {
                    addNoGuiParameter(sourceRunBat, targetRunBat, true);
                    Files.deleteIfExists(sourceRunBat);
                    System.out.println("Moved run.bat to " + targetRunBat);
                }

                Path sourceRunSh = Paths.get("run.sh");
                if (Files.exists(sourceRunSh)) {
                    addNoGuiParameter(sourceRunSh, targetRunSh, false);
                    Files.deleteIfExists(sourceRunSh);
                    System.out.println("Moved run.sh to " + targetRunSh);
                }
            } else {
                System.out.println("Script files already exist in target directory, updating version numbers.");
                Path sourceRunBat = Paths.get("run.bat");
                Path sourceRunSh = Paths.get("run.sh");
                if (Files.exists(sourceRunBat)) {
                    Files.deleteIfExists(sourceRunBat);
                    System.out.println("Deleted existing run.bat from root directory");
                }
                if (Files.exists(sourceRunSh)) {
                    Files.deleteIfExists(sourceRunSh);
                    System.out.println("Deleted existing run.sh from root directory");
                }
                updateScriptVersionNumbers(targetRunBat, targetRunSh);
            }
            
            Path installerLog = Paths.get("installer.jar.log");
            if (Files.exists(installerLog)) {
                Files.delete(installerLog);
                System.out.println("Deleted installer.jar.log from root directory");
            }

            String osName = System.getProperty("os.name").toLowerCase();
            ProcessBuilder pb;

            if (osName.contains("win")) {
                if (Files.exists(targetRunBat)) {
                    pb = new ProcessBuilder("cmd", "/c", targetRunBat.toAbsolutePath().toString());
                    pb.inheritIO();
                    System.out.println("Running server script: " + targetRunBat.toAbsolutePath());
                    pb.start();
                } else {
                    System.err.println("Windows batch file not found: " + targetRunBat);
                }
            } else {
                if (Files.exists(targetRunSh)) {
                    pb = new ProcessBuilder("sh", targetRunSh.toAbsolutePath().toString());
                    pb.inheritIO();
                    System.out.println("Running server script: " + targetRunSh.toAbsolutePath());
                    pb.start();
                } else {
                    System.err.println("Shell script not found: " + targetRunSh);
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to move or run server scripts: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void updateScriptVersionNumbers(Path targetRunBat, Path targetRunSh) throws IOException {
        if (NEOFORGE_VERSION == null) {
            System.err.println("Could not determine implementation version for script update");
            return;
        }
        
        if (Files.exists(targetRunBat)) {
            updateVersionInScript(targetRunBat, NEOFORGE_VERSION);
        }
        
        if (Files.exists(targetRunSh)) {
            updateVersionInScript(targetRunSh, NEOFORGE_VERSION);
        }
    }
    
    private static void updateVersionInScript(Path scriptPath, String newVersion) throws IOException {
        List<String> lines = Files.readAllLines(scriptPath);
        List<String> updatedLines = new ArrayList<>();
        boolean updated = false;
        String oldVersion = null;
        
        Pattern versionPattern = Pattern.compile("(/neoforge/)([\\d\\.\\-a-zA-Z]+)/");
        
        for (String line : lines) {
            Matcher matcher = versionPattern.matcher(line);
            if (matcher.find()) {
                try {
                    oldVersion = matcher.group(2);
                } catch (IllegalStateException e) {
                    oldVersion = "unknown";
                }
                String updatedLine = matcher.replaceAll("$1" + newVersion + "/");
                updatedLines.add(updatedLine);
                updated = true;
            } else {
                updatedLines.add(line);
            }
        }
        
        if (updated) {
            Files.write(scriptPath, updatedLines);
            System.out.println("Updated version in " + scriptPath.getFileName() + " from " + oldVersion + " to " + newVersion);
        }
    }

    private static void addNoGuiParameter(Path source, Path target, boolean isWindows) throws IOException {
        List<String> lines = Files.readAllLines(source);
        List<String> modifiedLines = new ArrayList<>();
        
        for (String line : lines) {
            if (line.trim().startsWith("java ")) {
                if (isWindows) {
                    if (line.contains("%*")) {
                        line = line.replace("%*", "-nogui %*");
                    }
                } else {
                    if (line.contains("\"$@\"")) {
                        line = line.replace("\"$@\"", "-nogui \"$@\"");
                    }
                }
            }
            modifiedLines.add(line);
        }
        
        Files.write(target, modifiedLines);
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