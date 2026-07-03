package org.teneted.neotenet.launcher.install;

import org.teneted.neotenet.launcher.data.McVersion;
import org.teneted.neotenet.launcher.data.VersionManifest;
import org.teneted.neotenet.launcher.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class ServerAction {

    private static final String versionRoot = "https://launchermeta.mojang.com/mc/game/version_manifest.json";


    public static void download(String version, File serverJar) throws IOException, URISyntaxException {
        VersionManifest manifest = Actions.mapper.readValue(new URI(versionRoot).toURL().openConnection().getInputStream(), VersionManifest.class);
        String versionUrl = manifest.versions().stream().filter(data -> data.id().equalsIgnoreCase(version))
                .findFirst()
                .orElseThrow()
                .url();
        McVersion mc = Actions.mapper.readValue(new URI(versionUrl).toURL().openConnection().getInputStream(), McVersion.class);
        FileUtils.download(mc.downloads().server().url(), serverJar);
        if (!FileUtils.checkFile(serverJar, mc.downloads().server().sha1())) {
            System.out.println("server jar download failed, delete it");
            serverJar.deleteOnExit();
        }
    }
}
