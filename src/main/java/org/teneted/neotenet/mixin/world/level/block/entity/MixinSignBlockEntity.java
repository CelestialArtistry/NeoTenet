package org.teneted.neotenet.mixin.world.level.block.entity;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.FilteredText;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.block.sign.Side;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.event.block.SignChangeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(SignBlockEntity.class)
public abstract class MixinSignBlockEntity extends BlockEntity implements CommandSource {

    @Shadow
    @Nullable
    private UUID playerWhoMayEdit;

    @Shadow
    protected abstract void clearInvalidPlayerWhoMayEdit(SignBlockEntity p_277656_, Level p_277853_, UUID p_277849_);

    @Shadow
    protected abstract SignText setMessages(Player p_277396_, List<FilteredText> p_277744_, SignText p_277359_);

    public MixinSignBlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    @WrapWithCondition(method = "markUpdated", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;sendBlockUpdated(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;I)V"))
    private boolean neotenet$checkLevel(Level instance, BlockPos blockPos, BlockState state, BlockState state1, int i) {
        return this.level != null;
    }

    @Inject(method = "getPlayerWhoMayEdit", at = @At("HEAD"))
    private void neotenet$clearInvalidPlayerWhoMayEdit(CallbackInfoReturnable<UUID> cir) {
        // CraftBukkit start - unnecessary sign ticking removed, so do this lazily
        if (this.level != null && this.playerWhoMayEdit != null) {
            clearInvalidPlayerWhoMayEdit(((SignBlockEntity) (Object) this), this.level, this.playerWhoMayEdit);
        }
        // CraftBukkit end
    }

    private AtomicBoolean neotenet$front = new AtomicBoolean();

    @Inject(method = "updateSignText", at = @At("HEAD"))
    private void neotenet$setFront(Player p_278048_, boolean p_278103_, List<FilteredText> p_277990_, CallbackInfo ci) {
        neotenet$front.set(p_278103_);
    }

    @Inject(method = "updateSignText", at = @At("RETURN"))
    private void neotenet$sendPacket(Player p_278048_, boolean p_278103_, List<FilteredText> p_277990_, CallbackInfo ci) {
        ((ServerPlayer) p_278048_).connection.send(this.getUpdatePacket()); // CraftBukkit
    }

    @Inject(method = "setMessages", at = @At("RETURN"), cancellable = true)
    private void neotenet$callSignChangeEvent(Player entityhuman, List<FilteredText> list, SignText signtext, CallbackInfoReturnable<SignText> cir) {
        // CraftBukkit start
        SignText originalText = signtext; // CraftBukkit
        org.bukkit.entity.Player player = ((ServerPlayer) entityhuman).getBukkitEntity();
        String[] lines = new String[4];

        for (int i = 0; i < list.size(); ++i) {
            lines[i] = CraftChatMessage.fromComponent(signtext.getMessage(i, entityhuman.isTextFilteringEnabled()));
        }

        SignChangeEvent event = new SignChangeEvent(CraftBlock.at(this.level, this.worldPosition), player, lines.clone(), (neotenet$front.get()) ? Side.FRONT : Side.BACK);
        entityhuman.level().getCraftServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            cir.setReturnValue(originalText);
        }

        Component[] components = org.bukkit.craftbukkit.block.CraftSign.sanitizeLines(event.getLines());
        for (int i = 0; i < components.length; i++) {
            if (!Objects.equals(lines[i], event.getLine(i))) {
                signtext = signtext.setMessage(i, components[i]);
            }
        }
        // CraftBukkit end
    }

    private SignText setMessages(Player p_277396_, List<FilteredText> p_277744_, SignText p_277359_, boolean front) { // CraftBukkit
        neotenet$front.set(front);
        return setMessages(p_277396_, p_277744_, p_277359_);
    }

    @ModifyArg(method = "createCommandSourceStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/CommandSourceStack;<init>(Lnet/minecraft/commands/CommandSource;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec2;Lnet/minecraft/server/level/ServerLevel;ILjava/lang/String;Lnet/minecraft/network/chat/Component;Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/world/entity/Entity;)V"), index = 0)
    private CommandSource neotenet$resetSource(CommandSource p_81302_) {
        return this;
    }

    // CraftBukkit start
    @Override
    public void sendSystemMessage(Component ichatbasecomponent) {}

    @Override
    public org.bukkit.command.CommandSender getBukkitSender(CommandSourceStack wrapper) {
        return wrapper.getEntity() != null ? wrapper.getEntity().getBukkitSender(wrapper) : new org.bukkit.craftbukkit.command.CraftBlockCommandSender(wrapper, this);
    }

    @Override
    public boolean acceptsSuccess() {
        return false;
    }

    @Override
    public boolean acceptsFailure() {
        return false;
    }

    @Override
    public boolean shouldInformAdmins() {
        return false;
    }
}
