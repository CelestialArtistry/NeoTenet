package org.taiyitistmc.mixin.world.item.crafting;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.taiyitistmc.injection.world.item.crafting.InjectionIngredient;

@Mixin(value = Ingredient.class)
public abstract class MixinIngredient implements InjectionIngredient {

    private final boolean isVanilla = ((Ingredient) (Object) this).getClass() == Ingredient.class;
    public boolean exact; // CraftBukkit

    @Shadow
    public abstract ItemStack[] getItems();

    @Override
    public boolean isVanilla() {
        return isVanilla;
    }

    @Inject(method = "test(Lnet/minecraft/world/item/ItemStack;)Z",
            at = @At("HEAD"),
            cancellable = true)
    private void taiyitist$test(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        for (ItemStack taiyitist$stack : this.getItems()) {
            // CraftBukkit start
            if (exact) {
                if (ItemStack.isSameItemSameComponents(taiyitist$stack, stack)) {
                    cir.setReturnValue(true);
                }
                continue;
            }
            if (taiyitist$stack.is(stack.getItem())) {
                cir.setReturnValue(true);
            }
            // CraftBukkit end
        }
    }
}
