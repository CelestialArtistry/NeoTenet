package org.celestial_artistry.neotenet.mixin.server.commands;

import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.commands.DifficultyCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.storage.WorldData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DifficultyCommand.class)
public class MixinDifficultyCommand {

    private static final AtomicReference<ServerLevel> neotenet$serverLevel = new AtomicReference<>();

    @Inject(method = "setDifficulty", at = @At("HEAD"))
    private static void neotenet$getServerLevel(CommandSourceStack source, Difficulty difficulty, CallbackInfoReturnable<Integer> cir) {
        neotenet$serverLevel.set(source.getLevel());
    }

    @Redirect(method = "setDifficulty",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/level/storage/WorldData;getDifficulty()Lnet/minecraft/world/Difficulty;"))
    private static Difficulty neotenet$getDifficult(WorldData instance) {
        return neotenet$serverLevel.getAndSet(null).getDifficulty();
    }

    @Redirect(method = "setDifficulty",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/server/MinecraftServer;setDifficulty(Lnet/minecraft/world/Difficulty;Z)V"))
    private static void neotenet$resetDifficulty(MinecraftServer instance, Difficulty difficulty, boolean forced) {
        neotenet$serverLevel.getAndSet(null).K.setDifficulty(difficulty);
    }
}
