package org.celestial_artistry.neotenet.mixin.server;

import com.google.common.collect.ImmutableMap;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerAdvancementManager;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(ServerAdvancementManager.class)
public class MixinServerAdvancementManager {

    @Shadow
    public Map<ResourceLocation, AdvancementHolder> advancements;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void neotenet$init(HolderLookup.Provider p_323943_, CallbackInfo ci) {
        this.advancements =  new HashMap<>(); // CraftBukkit - SPIGOT-7734: mutable
    }

    @Redirect(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At(value = "FIELD", target = "Lnet/minecraft/server/ServerAdvancementManager;advancements:Ljava/util/Map;", opcode = Opcodes.PUTFIELD))
    private void neotenet$mutableAdvancements(ServerAdvancementManager instance, Map<ResourceLocation, AdvancementHolder> value, @Local(name = "builder") ImmutableMap.Builder<ResourceLocation, AdvancementHolder> builder) {
        this.advancements = new HashMap<>(builder.buildOrThrow()); // CraftBukkit - SPIGOT-7734: mutable
    }
}
