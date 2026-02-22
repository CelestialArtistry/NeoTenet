package org.teneted.neotenet.mixin.world.level.block.entity;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.LecternMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LecternBlockEntity.class)
public abstract class MixinLecternBlockEntity extends BlockEntity implements CommandSource {

    @Shadow
    @Final
    private Container bookAccess;

    @Shadow
    @Final
    private ContainerData dataAccess;

    public MixinLecternBlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    // CraftBukkit start
    @Override
    public void sendSystemMessage(Component ichatbasecomponent) {
    }

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

    // CraftBukkit end

    @WrapWithCondition(method = "setPage", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/LecternBlock;signalPageChange(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V"))
    private boolean neotenet$checkLevelNPE(Level level, BlockPos blockPos, BlockState state) {
        return this.level != null;
    }

    @ModifyArg(method = "createCommandSourceStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/CommandSourceStack;<init>(Lnet/minecraft/commands/CommandSource;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec2;Lnet/minecraft/server/level/ServerLevel;ILjava/lang/String;Lnet/minecraft/network/chat/Component;Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/world/entity/Entity;)V"), index = 0)
    private CommandSource neotenet$resetCommandSourceStack(CommandSource p_81302_) {
        return this;
    }

    @ModifyReturnValue(method = "createMenu", at = @At("RETURN"))
    private AbstractContainerMenu neotenet$resetMenu(AbstractContainerMenu original, @Local(argsOnly = true) int p_59562_, @Local(argsOnly = true) Inventory p_59563_) {
        return new LecternMenu(p_59562_, this.bookAccess, this.dataAccess, p_59563_);
    }
}
