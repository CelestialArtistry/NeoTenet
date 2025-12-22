package org.celestial_artistry.neotenet.bukkit.remapping;

import org.objectweb.asm.tree.ClassNode;

public interface PluginTransformer {

    void handleClass(ClassNode node, ClassLoaderRemapper remapper, NeoTenetRemapConfig config);

    default int priority() {
        return 0;
    }
}