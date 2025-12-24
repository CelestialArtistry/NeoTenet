package org.teneted.neotenet.mixin.commands.arguments.selector;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import org.teneted.neotenet.injection.commands.arguments.selector.InjectionEntitySelectorParser;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntitySelectorParser.class)
public abstract class MixinEntitySelectorParser implements InjectionEntitySelectorParser {

    @Shadow protected abstract void parseSelector() throws CommandSyntaxException;

    @Shadow public abstract EntitySelector parse() throws CommandSyntaxException;

    @Shadow private boolean usesSelectors;

    @Shadow
    private AtomicBoolean  parseSelector$overridePermissions;
    @Shadow
    public AtomicBoolean parse$overridePermissions;

    @Override
    public void parseSelector(boolean overridePermissions) throws CommandSyntaxException {
        parseSelector$overridePermissions.set(overridePermissions);
        parseSelector();
    }

    @Redirect(method = "parseSelector", at = @At(value = "FIELD", target = "Lnet/minecraft/commands/arguments/selector/EntitySelectorParser;usesSelectors:Z"))
    private void taiyitist$resetUseSelectors(EntitySelectorParser instance, boolean value) {
        this.usesSelectors = !parseSelector$overridePermissions.getAndSet(false);
    }

    @Inject(method = "parse", at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/arguments/selector/EntitySelectorParser;parseSelector()V"))
    private void taiyitist$resetParseSelectors(CallbackInfoReturnable<EntitySelector> cir) throws CommandSyntaxException {
        parseSelector$overridePermissions.set(parse$overridePermissions.getAndSet(false));
    }

    @Override
    public EntitySelector parse(boolean overridePermissions) throws CommandSyntaxException {
        parseSelector$overridePermissions.set(overridePermissions);
        return parse();
    }
}
