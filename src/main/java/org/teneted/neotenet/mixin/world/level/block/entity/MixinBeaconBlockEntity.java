package org.teneted.neotenet.mixin.world.level.block.entity;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.phys.AABB;
import org.bukkit.craftbukkit.potion.CraftPotionUtil;
import org.bukkit.potion.PotionEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.teneted.neotenet.injection.world.level.block.entity.InjectionBeaconBlockEntity;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

@Mixin(BeaconBlockEntity.class)
public class MixinBeaconBlockEntity implements InjectionBeaconBlockEntity {

    @Shadow
    @Nullable
    private Holder<MobEffect> primaryPower;

    @Shadow
    private int levels;

    @Shadow
    @Nullable
    private Holder<MobEffect> secondaryPower;

    // CraftBukkit start - add fields and methods
    @Override
    public PotionEffect getPrimaryEffect() {
        return (this.primaryPower != null) ? CraftPotionUtil.toBukkit(new MobEffectInstance(this.primaryPower, getLevel(this.levels), getAmplification(levels, primaryPower, secondaryPower), true, true)) : null;
    }

    @Override
    public PotionEffect getSecondaryEffect() {
        return (hasSecondaryEffect(levels, primaryPower, secondaryPower)) ? CraftPotionUtil.toBukkit(new MobEffectInstance(this.secondaryPower, getLevel(this.levels), getAmplification(levels, primaryPower, secondaryPower), true, true)) : null;
    }
    // CraftBukkit end

    private static byte getAmplification(int i, @Nullable Holder<MobEffect> holder, @Nullable Holder<MobEffect> holder1) {
        byte b0 = 0;

        if (i >= 4 && Objects.equals(holder, holder1)) {
            b0 = 1;
        }

        return b0;
    }

    private static int getLevel(int i) {
        int j = (9 + i * 2) * 20;
        return j;
    }

    public static List getHumansInRange(Level world, BlockPos blockposition, int i) {
        double d0 = (double) (i * 10 + 10);

        AABB axisalignedbb = (new AABB(blockposition)).inflate(d0).expandTowards(0.0D, (double) world.getHeight(), 0.0D);
        List<Player> list = world.getEntitiesOfClass(Player.class, axisalignedbb);

        return list;
    }

    private static boolean hasSecondaryEffect(int i, @Nullable Holder<MobEffect> holder, @Nullable Holder<MobEffect> holder1) {
        if (i >= 4 && !Objects.equals(holder, holder1) && holder1 != null) {
            return true;
        }
        return false;
    }

    private static void applyEffect(List list, @Nullable Holder<MobEffect> holder, int j, int b0) {
        Iterator iterator = list.iterator();

        Player entityhuman;

        while (iterator.hasNext()) {
            entityhuman = (Player) iterator.next();
            entityhuman.addEffect(new MobEffectInstance(holder, j, b0, true, true));
            entityhuman.addEffect(new MobEffectInstance(holder, j, b0, true, true), org.bukkit.event.entity.EntityPotionEffectEvent.Cause.BEACON);
        }
    }

    private static void applyEffects(Level world, BlockPos blockposition, int i, @Nullable Holder<MobEffect> holder, @Nullable Holder<MobEffect> holder1) {
        if (!world.isClientSide && holder != null) {
            double d0 = (double) (i * 10 + 10);
            byte b0 = getAmplification(i, holder, holder1);

            int j = getLevel(i);
            List list = getHumansInRange(world, blockposition, i);

            applyEffect(list, holder, j, b0);

            if (hasSecondaryEffect(i, holder, holder1)) {
                applyEffect(list, holder1, j, 0);
            }
        }

    }
    // CraftBukkit end

    @Redirect(method = "loadEffect", at = @At(value = "INVOKE", target = "Ljava/util/Optional;map(Ljava/util/function/Function;)Ljava/util/Optional;"))
    private static <T, U> Optional neotenet$resetHolder(Optional instance, Function<? super T, ? extends U> mapper, @Local ResourceLocation resourcelocation) {
        return BuiltInRegistries.MOB_EFFECT.getHolder(resourcelocation); // CraftBukkit - persist manually set non-default beacon effects (SPIGOT-3598)
    }

    @Inject(method = "loadAdditional", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompoundTag;contains(Ljava/lang/String;I)Z"))
    private void neotenet$putLevels(CompoundTag p_338669_, HolderLookup.Provider p_338291_, CallbackInfo ci) {
        this.levels = p_338669_.getInt("Levels"); // CraftBukkit - SPIGOT-5053, use where available
    }
}
