package org.teneted.neotenet.mixin.commands.arguments.selector;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntitySelector.class)
public class MixinEntitySelector {

    @ModifyExpressionValue(method = "checkPermissions", at = @At(value = "INVOKE",
            target = "Lnet/neoforged/neoforge/common/CommonHooks;canUseEntitySelectors(Lnet/minecraft/commands/SharedSuggestionProvider;)Z"))
    private boolean taiyitist$checkPerm(boolean original, @Local(argsOnly = true) CommandSourceStack p_121169_) {
        return original && !p_121169_.hasPermission(2, "minecraft.command.selector");// CraftBukkit
    }
}
