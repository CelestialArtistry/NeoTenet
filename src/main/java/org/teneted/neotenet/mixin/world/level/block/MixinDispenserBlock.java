package org.teneted.neotenet.mixin.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DispenserBlock.class)
public class MixinDispenserBlock {

    @Shadow
    public static boolean eventFired;

    @Inject(method = "dispenseFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/DispenserBlockEntity;setItem(ILnet/minecraft/world/item/ItemStack;)V"))
    private void neotenet$setEventFired(ServerLevel p_52665_, BlockState p_302451_, BlockPos p_52666_, CallbackInfo ci) {
        eventFired = false; // CraftBukkit - reset event status
    }
}
