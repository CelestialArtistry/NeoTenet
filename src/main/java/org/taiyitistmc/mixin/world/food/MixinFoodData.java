package org.taiyitistmc.mixin.world.food;

import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.network.protocol.game.ClientboundSetHealthPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityExhaustionEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.taiyitistmc.injection.world.food.InjectionFoodData;

@Mixin(FoodData.class)
public abstract class MixinFoodData implements InjectionFoodData {


    private final AtomicBoolean duplicateCall = new AtomicBoolean(false);

    @Shadow
    protected abstract void add(int i, float f);

    @Shadow
    public int foodLevel;

    @Shadow
    private Player entityhuman;

    @Shadow
    private int lastFoodLevel;

    @Shadow
    public int starvationRate;

    @Shadow
    public int unsaturatedRegenRate;

    @Shadow
    public float saturationLevel;

    @Inject(method = "eat(IF)V", at = @At("HEAD"), cancellable = true)
    private void taiyitist$eatCake(int foodLevelModifier, float saturationLevelModifier, CallbackInfo ci) {
        // Taiyitist start
        if (!duplicateCall.getAndSet(false)) {
            int old = this.foodLevel;
            FoodLevelChangeEvent event = CraftEventFactory.callFoodLevelChangeEvent(entityhuman, old + foodLevelModifier);
            if (event.isCancelled()) ci.cancel();
            foodLevelModifier = event.getFoodLevel() - old;
        }
        // Taiyitist end
    }

    @Override
    public void eat(ItemStack itemstack, FoodProperties foodinfo) {
        int oldFoodLevel = foodLevel;

        FoodLevelChangeEvent event = CraftEventFactory.callFoodLevelChangeEvent(entityhuman, foodinfo.nutrition() + oldFoodLevel, itemstack);

        if (!event.isCancelled()) {
            this.add(event.getFoodLevel() - oldFoodLevel, foodinfo.saturation());
        }
        ((ServerPlayer) entityhuman).getBukkitEntity().sendHealthUpdate();
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(II)I"))
    private int taiyitist$foodLevelChange(int a, int b) throws Throwable {
        return 0;
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(II)I"))
    private void taiyitist$foodLevelChange1(Player player, CallbackInfo ci) {
        // CraftBukkit start
        org.bukkit.event.entity.FoodLevelChangeEvent event = org.bukkit.craftbukkit.event.CraftEventFactory.callFoodLevelChangeEvent(entityhuman, Math.max(this.foodLevel - 1, 0));

        if (!event.isCancelled()) {
            this.foodLevel = event.getFoodLevel();
        }

        ((ServerPlayer) entityhuman).connection.send(new ClientboundSetHealthPacket(((ServerPlayer) entityhuman).getBukkitEntity().getScaledHealth(), this.foodLevel, this.saturationLevel));
        // CraftBukkit end
    }

    @Inject(method = "tick", at = @At(value = "INVOKE_ASSIGN", remap = false, target = "Ljava/lang/Math;max(II)I"))
    public void taiyitist$foodLevelChange2(Player player, CallbackInfo ci) {
        if (entityhuman == null) {
            return;
        }
        FoodLevelChangeEvent event = CraftEventFactory.callFoodLevelChangeEvent(entityhuman, Math.max(this.lastFoodLevel - 1, 0));

        if (!event.isCancelled()) {
            this.foodLevel = event.getFoodLevel();
        } else {
            this.foodLevel = this.lastFoodLevel;
        }

        ((ServerPlayer) entityhuman).connection.send(new ClientboundSetHealthPacket(((ServerPlayer) entityhuman).getBukkitEntity().getScaledHealth(), this.foodLevel, this.saturationLevel));
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;heal(F)V"))
    public void taiyitist$heal(Player player, CallbackInfo ci) {
        if (entityhuman == null) {
            entityhuman = player;
        }
        player.pushHealReason(EntityRegainHealthEvent.RegainReason.SATIATED);
        player.pushExhaustReason(EntityExhaustionEvent.ExhaustionReason.REGEN);
    }

    @ModifyConstant(method = "tick", constant = @Constant(intValue = 10))
    private int taiyitist$changeValue(int constant) {
        return this.starvationRate; // CraftBukkit
    }

    @ModifyConstant(method = "tick", constant = @Constant(intValue = 80, ordinal = 0))
    private int taiyitist$changeValue0(int constant) {
        return this.unsaturatedRegenRate; // CraftBukkit - add regen rate manipulation
    }

    @ModifyConstant(method = "tick", constant = @Constant(intValue = 80, ordinal = 1))
    private int taiyitist$changeValue1(int constant) {
        return this.starvationRate;  // CraftBukkit - add regen rate manipulation
    }

    @Override
    public Player getEntityhuman() {
        return entityhuman;
    }

    @Override
    public void setEntityhuman(Player entityhuman) {
        this.entityhuman = entityhuman;
    }
}
