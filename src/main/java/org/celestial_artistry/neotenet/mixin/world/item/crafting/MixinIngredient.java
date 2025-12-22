package org.celestial_artistry.neotenet.mixin.world.item.crafting;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.celestial_artistry.neotenet.injection.world.item.crafting.InjectionIngredient;

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
    private void neotenet$test(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        for (ItemStack neotenet$stack : this.getItems()) {
            // CraftBukkit start
            if (exact) {
                if (ItemStack.isSameItemSameComponents(neotenet$stack, stack)) {
                    cir.setReturnValue(true);
                }
                continue;
            }
            if (neotenet$stack.is(stack.getItem())) {
                cir.setReturnValue(true);
            }
            // CraftBukkit end
        }
    }
}
