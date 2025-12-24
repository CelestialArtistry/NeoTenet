package org.teneted.neotenet.bukkit.remapping;

public interface RemappingClassLoader {

    ClassLoaderRemapper getRemapper();

    NeoTenetRemapConfig getRemapConfig();
}