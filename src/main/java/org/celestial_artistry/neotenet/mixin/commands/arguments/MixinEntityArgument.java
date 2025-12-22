package org.celestial_artistry.neotenet.mixin.commands.arguments;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.celestial_artistry.neotenet.injection.commands.arguments.InjectionEntityArgument;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(EntityArgument.class)
public abstract class MixinEntityArgument implements InjectionEntityArgument {

    @Shadow
    protected abstract EntitySelector parse(StringReader p_353134_, boolean p_353142_) throws CommandSyntaxException;

    @Shadow
    public AtomicBoolean parse$overridePermissions;

    @Override
    public EntitySelector parse(StringReader stringreader, boolean flag, boolean overridePermissions) throws CommandSyntaxException {
        parse$overridePermissions.set(overridePermissions);
        return parse(stringreader, flag);
    }

    @Inject(method = "parse(Lcom/mojang/brigadier/StringReader;Z)Lnet/minecraft/commands/arguments/selector/EntitySelector;", at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/arguments/selector/EntitySelectorParser;parse()Lnet/minecraft/commands/arguments/selector/EntitySelector;"))
    private void neotenet$markParse(StringReader p_353134_, boolean p_353142_, CallbackInfoReturnable<EntitySelector> cir, @Local(name = "entityselectorparser") EntitySelectorParser entityselectorparser) {
        entityselectorparser.parse$overridePermissions.set(parse$overridePermissions.getAndSet(false));
    }
}
