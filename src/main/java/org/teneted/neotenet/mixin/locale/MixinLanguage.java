package org.teneted.neotenet.mixin.locale;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.ResultConsumer;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.util.GsonHelper;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.teneted.neotenet.config.NeoTenetConfigUtil;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;

import static net.minecraft.server.ServerAdvancementManager.GSON;

@Mixin(Language.class)
public abstract class MixinLanguage {

    @Shadow
    @Final
    private static Logger LOGGER;
    @Final
    @Shadow
    private static Pattern UNSUPPORTED_FORMAT_PATTERN;


    @Shadow
    public static void loadFromJson(InputStream par1, BiConsumer<String, String> par2, BiConsumer<String, Component> par3) {
        JsonObject jsonobject = GSON.fromJson(new InputStreamReader(par1, StandardCharsets.UTF_8), JsonObject.class);

        for (Map.Entry<String, JsonElement> entry : jsonobject.entrySet()) {
            if (entry.getValue().isJsonArray()) {
                var component = net.minecraft.network.chat.ComponentSerialization.CODEC
                        .parse(com.mojang.serialization.JsonOps.INSTANCE, entry.getValue())
                        .getOrThrow(msg -> new com.google.gson.JsonParseException("Error parsing translation for " + entry.getKey() + ": " + msg));

                par2.accept(entry.getKey(), component.getString());
                par3.accept(entry.getKey(), component);

                continue;
            }

            String s = UNSUPPORTED_FORMAT_PATTERN.matcher(GsonHelper.convertToString(entry.getValue(), entry.getKey())).replaceAll("%$1s");
            par2.accept(entry.getKey(), s);
        }
    }

    @Redirect(method = "parseTranslations(Ljava/util/function/BiConsumer;Ljava/util/function/BiConsumer;Ljava/lang/String;)V", at = @At(value = "INVOKE", target = "Ljava/lang/Class;getResourceAsStream(Ljava/lang/String;)Ljava/io/InputStream;"))
    private static InputStream neotenet$useUserLanguage(Class instance, String e) {
        return Language.class.getResourceAsStream("/assets/minecraft/lang/%s.json".formatted(NeoTenetConfigUtil.lang().toLowerCase(Locale.ROOT)));
    }
}

