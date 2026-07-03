package org.teneted.neotenet.launcher.data;

public record LibraryArtifact(
        String sha1,
        long size,
        String url,
        String path
) {

}
