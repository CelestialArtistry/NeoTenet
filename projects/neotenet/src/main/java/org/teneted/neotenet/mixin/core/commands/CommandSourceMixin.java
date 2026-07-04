package org.teneted.neotenet.mixin.core.commands;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.neotenet.injection.commands.CommandSourceInjection;

@Mixin(CommandSource.class)
public interface CommandSourceMixin extends CommandSourceInjection {

    @Override
    org.bukkit.command.CommandSender getBukkitSender(CommandSourceStack wrapper);
}
