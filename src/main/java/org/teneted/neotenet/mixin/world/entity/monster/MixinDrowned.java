package org.teneted.neotenet.mixin.world.entity.monster;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Drowned.class)
public abstract class MixinDrowned extends Zombie implements RangedAttackMob {

    public MixinDrowned(EntityType<? extends Zombie> p_34271_, Level p_34272_) {
        super(p_34271_, p_34272_);
    }

    @ModifyArg(method = "performRangedAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/ThrownTrident;<init>(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;)V"), index = 2)
    private ItemStack neotenet$useTrident(ItemStack p_37571_) {
        return this.getItemInHand(net.minecraft.world.entity.projectile.ProjectileUtil.getWeaponHoldingHand(this, Items.TRIDENT)); // CraftBukkit - Use Trident in hand like skeletons (SPIGOT-7025)
    }
}
