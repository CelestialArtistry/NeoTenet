package org.celestial_artistry.neotenet.mixin.world.item;

import com.llamalad7.mixinextras.sugar.Cancellable;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArmorItem.class)
public class MixinArmorItem {

    @Redirect(method = "dispenseArmor", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setItemSlot(Lnet/minecraft/world/entity/EquipmentSlot;Lnet/minecraft/world/item/ItemStack;)V"))
    private static void neotenet$blockDispenseArmorEvent(LivingEntity instance, EquipmentSlot equipmentSlot, ItemStack itemStack, @Local(argsOnly = true) BlockSource p_302421_, @Local(argsOnly = true) ItemStack p_40400_, @Cancellable CallbackInfoReturnable<Boolean> cir) {
        // CraftBukkit start
        Level world = p_302421_.level();
        org.bukkit.block.Block block = CraftBlock.at(world, p_302421_.pos());
        CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemStack);
        BlockDispenseArmorEvent event = new BlockDispenseArmorEvent(block, craftItem.clone(), (org.bukkit.craftbukkit.entity.CraftLivingEntity) instance.getBukkitEntity());
        if (!DispenserBlock.eventFired) {
            world.getCraftServer().getPluginManager().callEvent(event);
        }

        if (event.isCancelled()) {
            p_40400_.grow(1);
            cir.setReturnValue(false);
        }

        if (!event.getItem().equals(craftItem)) {
            p_40400_.grow(1);
            // Chain to handler for new item
            ItemStack eventStack = CraftItemStack.asNMSCopy(event.getItem());
            DispenseItemBehavior idispensebehavior = (DispenseItemBehavior) DispenserBlock.DISPENSER_REGISTRY.get(eventStack.getItem());
            if (idispensebehavior != DispenseItemBehavior.NOOP && idispensebehavior != ArmorItem.DISPENSE_ITEM_BEHAVIOR) {
                idispensebehavior.dispense(p_302421_, eventStack);
                cir.setReturnValue(true);
            }
        }

        instance.setItemSlot(equipmentSlot, CraftItemStack.asNMSCopy(event.getItem()));
        // CraftBukkit endS
    }
}
