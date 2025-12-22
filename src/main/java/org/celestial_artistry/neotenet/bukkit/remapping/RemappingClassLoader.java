package org.celestial_artistry.neotenet.bukkit.remapping;

public interface RemappingClassLoader {

    ClassLoaderRemapper getRemapper();

    NeoTenetRemapConfig getRemapConfig();
}