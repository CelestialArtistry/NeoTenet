package org.teneted.neotenet.mixin.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.bukkit.event.player.PlayerSignOpenEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.teneted.neotenet.injection.world.level.block.InjectionSignBlock;

import java.util.concurrent.atomic.AtomicReference;

@Mixin(SignBlock.class)
public abstract class MixinSignBlock extends BaseEntityBlock implements InjectionSignBlock {

    @Shadow
    public abstract void openTextEdit(Player p_277738_, SignBlockEntity p_277467_, boolean p_277771_);

    protected MixinSignBlock(Properties p_49224_) {
        super(p_49224_);
    }

    @Unique
    private AtomicReference<PlayerSignOpenEvent.Cause> signCause = new AtomicReference<>(PlayerSignOpenEvent.Cause.UNKNOWN);

    @Inject(method = "useWithoutItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/SignBlock;openTextEdit(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/block/entity/SignBlockEntity;Z)V"))
    private void neotenet$interactCause(BlockState p_316779_, Level p_316615_, BlockPos p_316127_, Player p_316173_, BlockHitResult p_316850_, CallbackInfoReturnable<InteractionResult> cir) {
        signCause.getAndSet(PlayerSignOpenEvent.Cause.INTERACT);
    }

    @Inject(method = "openTextEdit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/SignBlockEntity;setAllowedPlayerEditor(Ljava/util/UUID;)V"), cancellable = true)
    private void neotenet$callPlayerSignOpenEvent(Player p_277738_, SignBlockEntity p_277467_, boolean p_277771_, CallbackInfo ci) {
        if (!org.bukkit.craftbukkit.event.CraftEventFactory.callPlayerSignOpenEvent(p_277738_, p_277467_, p_277771_, signCause.get() != null ? signCause.get() : signCause.getAndSet(PlayerSignOpenEvent.Cause.UNKNOWN))) {
            ci.cancel();
            return;
        }
        // Craftbukkit end
    }

    @Override
    public void pushOpenSignCause(PlayerSignOpenEvent.Cause cause) {
        this.signCause.getAndSet(cause);
    }

    @Override
    public void openTextEdit(Player p_277738_, SignBlockEntity p_277467_, boolean p_277771_, PlayerSignOpenEvent.Cause cause) {
        this.signCause.getAndSet(PlayerSignOpenEvent.Cause.UNKNOWN);
        openTextEdit(p_277738_, p_277467_, p_277771_);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<T> p_153214_) {
        return null; // Craftbukkit - remove unnecessary sign ticking
    }
}
