package org.teneted.neotenet.mixin.locale;

import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.teneted.neotenet.config.NeoTenetConfigUtil;

import java.io.InputStream;
import java.util.Locale;
import java.util.function.BiConsumer;

@Mixin(Language.class)
public abstract class MixinLanguage {

    @Shadow
    @Final
    private static Logger LOGGER;


    @Shadow
    public static void loadFromJson(InputStream par1, BiConsumer<String, String> par2, BiConsumer<String, Component> par3) {

    }

    @Redirect(method = "parseTranslations(Ljava/util/function/BiConsumer;Ljava/util/function/BiConsumer;Ljava/lang/String;)V", at = @At(value = "INVOKE", target = "Ljava/lang/Class;getResourceAsStream(Ljava/lang/String;)Ljava/io/InputStream;"))
    private static InputStream neotenet$useUserLanguage(Class instance, String e) {
        return Language.class.getResourceAsStream("/assets/minecraft/lang/%s.json".formatted(NeoTenetConfigUtil.lang().toLowerCase(Locale.ROOT)));
    }
}

