package org.teneted.neotenet.mixin;

import io.izzel.arclight.mixin.MixinTools;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.teneted.neotenet.NeoTenet;

import java.util.List;
import java.util.Set;

public class NeoTenetMixinPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String mixinPackage) {
        try {
            MixinTools.setup();
            NeoTenet.run();
        } catch (Exception ex) {
            NeoTenet.LOGGER.error("Failed to load NeoTenet Server..., caused by " + ex.getCause());
            throw new RuntimeException(ex);
        }
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
