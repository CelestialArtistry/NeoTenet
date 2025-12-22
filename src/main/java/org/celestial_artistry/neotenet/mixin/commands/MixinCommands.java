package org.celestial_artistry.neotenet.mixin.commands;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.ContextChain;
import com.mojang.brigadier.tree.CommandNode;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import com.mojang.brigadier.tree.RootCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.network.protocol.game.ClientboundCommandsPacket;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.spigotmc.SpigotConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.celestial_artistry.neotenet.injection.commands.InjectionCommandNode;
import org.celestial_artistry.neotenet.injection.commands.InjectionCommands;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(Commands.class)
public abstract class MixinCommands implements InjectionCommands {

    @Mutable
    @Shadow
    private CommandDispatcher<CommandSourceStack> dispatcher;

    @Shadow
    public abstract void performCommand(ParseResults<CommandSourceStack> parseResults, String string);

    @Shadow
    @Nullable
    private static ContextChain<CommandSourceStack> finishParsing(ParseResults<CommandSourceStack> p_307220_, String p_307311_, CommandSourceStack p_307676_) {
        return null;
    }

    @Shadow
    private static AtomicReference<String> neotenet$lable;
    @Unique
    private static final AtomicReference<String> finishParsing$label = new AtomicReference<>();
    @Final
    @Shadow
    private AtomicBoolean performCommand$throwCommandError;

    @Override
    public void performCommandCB(ParseResults<CommandSourceStack> parseresults, String s, String label, boolean throwCommandError) {
        neotenet$lable.set(label);
        performCommand$throwCommandError.set(false);
        this.performCommand(parseresults, s);
    }

    @Override
    public void performCommandCB(ParseResults<CommandSourceStack> pParseResults, String pCommand, String label) { // CraftBukkit
        neotenet$lable.set(label);
        performCommand$throwCommandError.set(false);
        this.performCommand(pParseResults, pCommand);
    }

    @Nullable
    private static ContextChain<CommandSourceStack> finishParsing(ParseResults<CommandSourceStack> p_307220_, String p_307311_, CommandSourceStack p_307676_, String label) { // CraftBukkit
        neotenet$lable.set(label);
        return finishParsing(p_307220_, p_307311_, p_307676_);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void performPrefixedCommand(CommandSourceStack p_230958_, String p_230959_) {
        this.performPrefixedCommand(p_230958_, p_230959_, p_230959_);
    }

    @Override
    public void performPrefixedCommand(CommandSourceStack commandSourceStack, String s, String label) {
        s = s.startsWith("/") ? s.substring(1) : s;
        this.performCommandCB(this.dispatcher.parse(s, commandSourceStack), s, label);
        // CraftBukkit end
    }

    @Override
    public void dispatchServerCommand(CommandSourceStack sender, String command) {
        Joiner joiner = Joiner.on(" ");
        if (command.startsWith("/")) {
            command = command.substring(1);
        }

        ServerCommandEvent event = new ServerCommandEvent(sender.getBukkitSender(), command);
        org.bukkit.Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        command = event.getCommand();

        String[] args = command.split(" ");

        String cmd = args[0];
        if (cmd.startsWith("minecraft:")) cmd = cmd.substring("minecraft:".length());
        if (cmd.startsWith("bukkit:")) cmd = cmd.substring("bukkit:".length());

        // Block disallowed commands
        if (cmd.equalsIgnoreCase("stop") || cmd.equalsIgnoreCase("kick") || cmd.equalsIgnoreCase("op")
                || cmd.equalsIgnoreCase("deop") || cmd.equalsIgnoreCase("ban") || cmd.equalsIgnoreCase("ban-ip")
                || cmd.equalsIgnoreCase("pardon") || cmd.equalsIgnoreCase("pardon-ip") || cmd.equalsIgnoreCase("reload")) {
            return;
        }

        // Handle vanilla commands;
        if (sender.getLevel().getCraftServer().getCommandBlockOverride(args[0])) {
            args[0] = "minecraft:" + args[0];
        }

        String newCommand = joiner.join(args);
        this.performPrefixedCommand(sender, newCommand, newCommand);
    }

    @Inject(method = "performCommand", at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/Commands;finishParsing(Lcom/mojang/brigadier/ParseResults;Ljava/lang/String;Lnet/minecraft/commands/CommandSourceStack;)Lcom/mojang/brigadier/context/ContextChain;"))
    private void neotenet$setLabel(ParseResults<CommandSourceStack> p_242844_, String p_242841_, CallbackInfo ci) {
        var label = neotenet$lable.get();
        finishParsing$label.set(label);
    }

    /**
     * @author wdog5
     * @reason PlayerCommandSendEvent
     */
    @Overwrite
    public void sendCommands(ServerPlayer player) {
        if (SpigotConfig.tabComplete < 0) return;
        Map<CommandNode<CommandSourceStack>, CommandNode<SharedSuggestionProvider>> map = Maps.newIdentityHashMap();

        RootCommandNode<SharedSuggestionProvider> vanillaRoot = new RootCommandNode<>();
        Commands vanillaCommands =  player.server.vanillaCommandDispatcher;
        map.put(vanillaCommands.getDispatcher().getRoot(), vanillaRoot);

        // FORGE: Use our own command node merging method to handle redirect nodes properly, see issue #7551
        net.neoforged.neoforge.server.command.CommandHelper.mergeCommandNode(this.dispatcher.getRoot(), vanillaRoot, map, player.createCommandSourceStack(), ctx -> 0, suggest -> SuggestionProviders.safelySwap((com.mojang.brigadier.suggestion.SuggestionProvider<SharedSuggestionProvider>) (com.mojang.brigadier.suggestion.SuggestionProvider<?>) suggest));

        RootCommandNode<SharedSuggestionProvider> node = new RootCommandNode<>();
        map.put(this.dispatcher.getRoot(), node);
        net.neoforged.neoforge.server.command.CommandHelper.mergeCommandNode(this.dispatcher.getRoot(), node, map, player.createCommandSourceStack(), ctx -> 0, suggest -> SuggestionProviders.safelySwap((com.mojang.brigadier.suggestion.SuggestionProvider<SharedSuggestionProvider>) (com.mojang.brigadier.suggestion.SuggestionProvider<?>) suggest));

        LinkedHashSet<String> set = new LinkedHashSet<>();
        for (CommandNode<SharedSuggestionProvider> child : node.getChildren()) {
            set.add(child.getName());
        }
        PlayerCommandSendEvent event = new PlayerCommandSendEvent(player.getBukkitEntity(), new LinkedHashSet<>(set));
        Bukkit.getPluginManager().callEvent(event);
        for (String s : set) {
            if (!event.getCommands().contains(s)) {
                ((InjectionCommandNode) node).removeCommand(s);
            }
        }
        player.connection.send(new ClientboundCommandsPacket(node));
    }
}
