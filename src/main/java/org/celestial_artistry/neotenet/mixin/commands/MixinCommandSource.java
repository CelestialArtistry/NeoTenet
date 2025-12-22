package org.celestial_artistry.neotenet.mixin.commands;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.command.ServerCommandSender;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.celestial_artistry.neotenet.injection.commands.InjectionCommandSource;

@Mixin(CommandSource.class)
public interface MixinCommandSource extends InjectionCommandSource {

     default CommandSender neotenet$getBukkitSender(CommandSourceStack wrapper) {
        return new ServerCommandSender() {
            private final boolean isOp = wrapper.hasPermission(wrapper.getServer().getOperatorUserPermissionLevel());

            @Override
            public boolean isOp() {
                return isOp;
            }

            @Override
            public void setOp(boolean value) {
            }

            @Override
            public void sendMessage(@NotNull String message) {

            }

            @Override
            public void sendMessage(@NotNull String[] messages) {

            }

            @NotNull
            @Override
            public String getName() {
                return "NULL";
            }
        };
    }

    @Override
    default CommandSender getBukkitSender(CommandSourceStack wrapper) {
        return neotenet$getBukkitSender(wrapper);
    }
}
