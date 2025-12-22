package org.celestial_artistry.neotenet.bukkit.remapping;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/*
 * Used to record transformation detail for specific ClassLoaders.
 */
public record NeoTenetRemapConfig(boolean remap) {
    public static final NeoTenetRemapConfig PLUGIN = new NeoTenetRemapConfig(true);

    public NeoTenetRemapConfig copy() {
        return new NeoTenetRemapConfig(remap);
    }

    public int write(DataOutput output) throws IOException {
        output.writeBoolean(remap);
        return 1;
    }

    public static NeoTenetRemapConfig read(DataInput input) throws IOException {
        return new NeoTenetRemapConfig(input.readBoolean());
    }
}