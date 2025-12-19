// CHECKSTYLE:OFF
package org.bukkit.plugin.java;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class LibraryLoader {

    private final Logger logger;
    private final File librariesDirectory;

    public LibraryLoader(@NotNull Logger logger) {
        this.logger = logger;
        this.librariesDirectory = new File("libraries");
        if (!librariesDirectory.exists()) {
            if (!librariesDirectory.mkdirs()) {
                logger.log(Level.WARNING, "Failed to create libraries directory: {0}", librariesDirectory.getAbsolutePath());
            }
        }
    }

    @Nullable
    public ClassLoader createLoader(@NotNull PluginDescriptionFile desc) {
        if (desc.getLibraries().isEmpty()) {
            return null;
        }

        logger.log(Level.INFO, "[{0}] Loading {1} libraries... please wait", new Object[]{
                desc.getName(), desc.getLibraries().size()
        });

        List<URL> jarFiles = new ArrayList<>();
        for (String library : desc.getLibraries()) {
            try {
                URL jarUrl = resolveLibrary(library);
                jarFiles.add(jarUrl);
                logger.log(Level.INFO, "[{0}] Loaded library {1}", new Object[]{
                        desc.getName(), library
                });
            } catch (Exception e) {
                throw new RuntimeException("Error resolving library: " + library, e);
            }
        }

        URLClassLoader loader = new URLClassLoader(
                jarFiles.toArray(new URL[0]),
                getClass().getClassLoader()
        );

        return loader;
    }

    private URL resolveLibrary(String library) throws IOException {
        Pattern pattern = Pattern.compile("([^:]+):([^:]+):([^:]+)");
        Matcher matcher = pattern.matcher(library);

        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid library format: " + library);
        }

        String groupId = matcher.group(1);
        String artifactId = matcher.group(2);
        String version = matcher.group(3);

        String groupPath = groupId.replace('.', '/');
        String fileName = artifactId + "-" + version + ".jar";
        File localFile = new File(librariesDirectory, groupPath + "/" + artifactId + "/" + version + "/" + fileName);

        if (!localFile.exists()) {
            downloadLibrary(groupId, artifactId, version, localFile);
        }

        return localFile.toURI().toURL();
    }

    private void downloadLibrary(String groupId, String artifactId, String version, File destination) throws IOException {
        File parentDir = destination.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                throw new IOException("Failed to create directory: " + parentDir.getAbsolutePath());
            }
        }

        String groupPath = groupId.replace('.', '/');
        String fileName = artifactId + "-" + version + ".jar";
        String downloadUrl = "https://repo.maven.apache.org/maven2/" + groupPath + "/" + artifactId + "/" + version + "/" + fileName;

        logger.log(Level.INFO, "Downloading {0}", downloadUrl);

        URL url = URI.create(downloadUrl).toURL();
        try (ReadableByteChannel rbc = Channels.newChannel(url.openStream());
             FileOutputStream fos = new FileOutputStream(destination)) {
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        }
    }
}