package org.teneted.neotenet.mixin.world.level.block.entity;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;

@Pseudo
@Mixin(targets = "net.minecraft.world.level.block.entity.CommandBlockEntity$1")
public abstract class MixinCommandBlockEntity_1 implements CommandSource {

    @Shadow
    @Final
    private CommandBlockEntity this$0;

    // CraftBukkit start
    @Override
    public org.bukkit.command.CommandSender getBukkitSender(CommandSourceStack wrapper) {
        return new org.bukkit.craftbukkit.command.CraftBlockCommandSender(wrapper, this$0);
    }
    // CraftBukkit end
}
