package org.celestial_artistry.neotenet.mixin.server.commands;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.commands.WeatherCommand;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WeatherCommand.class)
public class MixinWeatherCommand {

    @Redirect(method = "getDuration", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;overworld()Lnet/minecraft/server/level/ServerLevel;"))
    private static ServerLevel neotenet$preWorldDuration(MinecraftServer instance, @Local(argsOnly = true) CommandSourceStack sourceStack) {
        return sourceStack.getLevel();
    }

    @Redirect(method = "setClear", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;overworld()Lnet/minecraft/server/level/ServerLevel;"))
    private static ServerLevel neotenet$preWorldClear(MinecraftServer instance, @Local(argsOnly = true) CommandSourceStack sourceStack) {
        return sourceStack.getLevel();
    }

    @Redirect(method = "setRain", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;overworld()Lnet/minecraft/server/level/ServerLevel;"))
    private static ServerLevel neotenet$preWorldRain(MinecraftServer instance, @Local(argsOnly = true) CommandSourceStack sourceStack) {
        return sourceStack.getLevel();
    }

    @Redirect(method = "setThunder", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;overworld()Lnet/minecraft/server/level/ServerLevel;"))
    private static ServerLevel neotenet$preWorldThunder(MinecraftServer instance, @Local(argsOnly = true) CommandSourceStack sourceStack) {
        return sourceStack.getLevel();
    }
}
