package org.teneted.neotenet.mixin.world.item;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SnowballItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SnowballItem.class)
public class MixinSnowballItem extends Item {

    public MixinSnowballItem(Properties properties) {
        super(properties);
    }

    @Redirect(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean neotenet$addEntity(Level instance, Entity entity, @Local(argsOnly = true) Player p_43143_, @Local(ordinal = 0) ItemStack itemstack) {
        if (instance.addFreshEntity(entity)) {
            itemstack.consume(1, p_43143_);

            instance.playSound((Player) null, p_43143_.getX(), p_43143_.getY(), p_43143_.getZ(), SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (instance.getRandom().nextFloat() * 0.4F + 0.8F));
        } else if (p_43143_ instanceof net.minecraft.server.level.ServerPlayer) {
            ((net.minecraft.server.level.ServerPlayer) p_43143_).getBukkitEntity().updateInventory();
        }
        return true;
    }
}
