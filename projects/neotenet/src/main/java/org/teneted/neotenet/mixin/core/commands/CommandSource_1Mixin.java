package org.teneted.neotenet.mixin.core.commands;

import net.minecraft.commands.CommandSourceStack;
import org.bukkit.craftbukkit.command.NullCommandSender;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.neotenet.injection.commands.CommandSourceInjection;

@Mixin(targets = "net.minecraft.commands.CommandSource$1")
public class CommandSource_1Mixin implements CommandSourceInjection {

    // CraftBukkit start
    @Override
    public org.bukkit.command.CommandSender getBukkitSender(CommandSourceStack wrapper) {
        return NullCommandSender.INSTANCE;
    }
    // CraftBukkit end
}