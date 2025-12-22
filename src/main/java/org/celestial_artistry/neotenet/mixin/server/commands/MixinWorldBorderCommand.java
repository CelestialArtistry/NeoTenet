package org.celestial_artistry.neotenet.mixin.server.commands;

import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.commands.WorldBorderCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.Vec2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldBorderCommand.class)
public class MixinWorldBorderCommand {

    @Unique
    private static final AtomicReference<CommandSourceStack> neotenet$source = new AtomicReference<>();

    @Inject(method = "setDamageBuffer", at = @At("HEAD"))
    private static void neotenet$setSource(CommandSourceStack source, float distance, CallbackInfoReturnable<Integer> cir) {
        neotenet$source.set(source);
    }

    @Redirect(method = "setDamageBuffer", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerLevel;getWorldBorder()Lnet/minecraft/world/level/border/WorldBorder;"))
    private static WorldBorder neotenet$resetBorder(ServerLevel instance) {
        return neotenet$source.get().getLevel().getWorldBorder();
    }

    @Inject(method = "setDamageAmount", at = @At("HEAD"))
    private static void neotenet$setSource0(CommandSourceStack source, float distance, CallbackInfoReturnable<Integer> cir) {
        neotenet$source.set(source);
    }

    @Redirect(method = "setDamageAmount", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerLevel;getWorldBorder()Lnet/minecraft/world/level/border/WorldBorder;"))
    private static WorldBorder neotenet$resetBorder0(ServerLevel instance) {
        return neotenet$source.get().getLevel().getWorldBorder();
    }

    @Inject(method = "setWarningTime", at = @At("HEAD"))
    private static void neotenet$setSource1(CommandSourceStack source, int time, CallbackInfoReturnable<Integer> cir) {
        neotenet$source.set(source);
    }

    @Redirect(method = "setWarningTime", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerLevel;getWorldBorder()Lnet/minecraft/world/level/border/WorldBorder;"))
    private static WorldBorder neotenet$resetBorder1(ServerLevel instance) {
        return neotenet$source.get().getLevel().getWorldBorder();
    }

    @Inject(method = "setWarningDistance", at = @At("HEAD"))
    private static void neotenet$setSource2(CommandSourceStack source, int time, CallbackInfoReturnable<Integer> cir) {
        neotenet$source.set(source);
    }

    @Redirect(method = "setWarningDistance", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerLevel;getWorldBorder()Lnet/minecraft/world/level/border/WorldBorder;"))
    private static WorldBorder neotenet$resetBorder2(ServerLevel instance) {
        return neotenet$source.get().getLevel().getWorldBorder();
    }

    @Inject(method = "getSize", at = @At("HEAD"))
    private static void neotenet$setSource3(CommandSourceStack source, CallbackInfoReturnable<Integer> cir) {
        neotenet$source.set(source);
    }

    @Redirect(method = "getSize", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerLevel;getWorldBorder()Lnet/minecraft/world/level/border/WorldBorder;"))
    private static WorldBorder neotenet$resetBorder3(ServerLevel instance) {
        return neotenet$source.get().getLevel().getWorldBorder();
    }

    @Inject(method = "setCenter", at = @At("HEAD"))
    private static void neotenet$setSource4(CommandSourceStack source, Vec2 pos, CallbackInfoReturnable<Integer> cir) {
        neotenet$source.set(source);
    }

    @Redirect(method = "setCenter", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerLevel;getWorldBorder()Lnet/minecraft/world/level/border/WorldBorder;"))
    private static WorldBorder neotenet$resetBorder4(ServerLevel instance) {
        return neotenet$source.get().getLevel().getWorldBorder();
    }

    @Inject(method = "setSize", at = @At("HEAD"))
    private static void neotenet$setSource5(CommandSourceStack source, double newSize, long time, CallbackInfoReturnable<Integer> cir) {
        neotenet$source.set(source);
    }

    @Redirect(method = "setSize", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerLevel;getWorldBorder()Lnet/minecraft/world/level/border/WorldBorder;"))
    private static WorldBorder neotenet$resetBorder5(ServerLevel instance) {
        return neotenet$source.get().getLevel().getWorldBorder();
    }
}
