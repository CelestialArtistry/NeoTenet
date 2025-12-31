package org.teneted.neotenet.mixin.world.level.block;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.WitherSkullBlock;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WitherSkullBlock.class)
public class MixinWitherSkullBlock {

    @Inject(method = "checkSpawn(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/SkullBlockEntity;)V", at = @At("HEAD"), cancellable = true)
    private static void neotenet$capture(Level p_58256_, BlockPos p_58257_, SkullBlockEntity p_58258_, CallbackInfo ci) {
        if (p_58256_.captureBlockStates) ci.cancel(); return; // CraftBukkit
    }

    @Inject(method = "checkSpawn(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/SkullBlockEntity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getEntitiesOfClass(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"), cancellable = true)
    private static void neotenet$pushReason(Level p_58256_, BlockPos p_58257_, SkullBlockEntity p_58258_, CallbackInfo ci, @Local BlockPattern.BlockPatternMatch blockpattern$blockpatternmatch, @Local WitherBoss witherboss) {
        // CraftBukkit start
        if (!p_58256_.addFreshEntity(witherboss, CreatureSpawnEvent.SpawnReason.BUILD_WITHER)) {
            ci.cancel();
            return;
        }
        CarvedPumpkinBlock.clearPatternBlocks(p_58256_, blockpattern$blockpatternmatch); // CraftBukkit - from above
        // CraftBukkit end
    }

    @Redirect(method = "checkSpawn(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/SkullBlockEntity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/CarvedPumpkinBlock;clearPatternBlocks(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/block/state/pattern/BlockPattern$BlockPatternMatch;)V"))
    private static void neotenet$moveDown(Level level, BlockPattern.BlockPatternMatch blockPatternMatch) {}

    @Redirect(method = "checkSpawn(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/SkullBlockEntity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private static boolean neotenet$moveUp(Level instance, Entity entity) {
        return false;
    }
}
