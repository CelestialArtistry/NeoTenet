package org.teneted.neotenet.launcher.install;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import cpw.mods.bootstraplauncher.BootstrapLauncher;
import org.teneted.neotenet.launcher.NeoTenetAgent;
import org.teneted.neotenet.launcher.utils.FileUtils;
import org.teneted.neotenet.launcher.data.InstallProfile;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Actions {

    private static final List<String> neededFiles = new ArrayList<>() {{
        add("server.lzma");
        add("install_profile.json");
        add("neoforge-21.1.217-universal.jar");
    }};

    private static final ObjectMapper mapper = new ObjectMapper();

    public static void init() throws Throwable {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        URI file = Actions.class.getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .toURI();
        if (file.getPath().endsWith(".jar")) {
            // NeoTent - for jar
            System.out.println("Extracting installer jar...");
            JarFile jar = new JarFile(new File(file));
            Optional<JarEntry> installEntry = jar.stream().filter((entry) -> !entry.isDirectory() && entry.getName().endsWith("-installer.jar"))
                    .findFirst();
            if (installEntry.isPresent()) {
                System.out.println("Extracting installer files form installer jar...");
                File installJarFile = FileUtils.copyToTempFile(jar.getInputStream(installEntry.get()));
                JarFile installJar = new JarFile(installJarFile);
                List<JarEntry> files = installJar.stream().filter(entry -> !entry.isDirectory() && neededFiles.contains(entry.getName())).toList();
                InstallProfile profile = mapper.readValue(installJar.getInputStream(files.stream()
                        .filter(entry -> entry.getName().startsWith("install_profile"))
                        .findFirst()
                        .get()), InstallProfile.class);
                File librariesDir = new File("libraries");
                // NeoTent - wait for download
                System.out.println("Download libraries...");
                LibrariesAction.download(profile.libraries(), librariesDir);
                // NeoTent - add module
                NeoTenetAgent.instrumentation.redefineModule(ModuleLayer.boot().findModule("java.base").orElseThrow(),
                        Set.of(),
                        Map.of(),
                        Map.of("java.lang", Set.of(Actions.class.getModule())),
                        Set.of(),
                        Map.of());
                // NeoTent - add to path
                String srcClassPath = System.getProperty("java.class.path");
                StringBuilder classPath = new StringBuilder();
                classPath.append(srcClassPath);
                profile.libraries().forEach(library -> {
                    String path = new File(librariesDir, library.downloads().artifact().path()).getPath();
                    classPath.append(File.pathSeparatorChar).append(path);
                    try {
                        NeoTenetAgent.instrumentation.appendToSystemClassLoaderSearch(new JarFile(path));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                System.setProperty("java.class.path", classPath.toString());
                NeoTenetAgent.instrumentation.redefineModule(ModuleLayer.boot().findModule("java.base").orElseThrow(),
                        Set.of(),
                        Map.of(),
                        Map.of("java.lang", Set.of(BootstrapLauncher.class.getModule())),
                        Set.of(),
                        Map.of());
            } else {
                throw new RuntimeException("Installer jar not exist or broken");
            }
        } else {
            // NeoTent - for project
            throw new RuntimeException("Not support");
        }
    }

}
