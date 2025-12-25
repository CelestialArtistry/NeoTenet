package org.teneted.neotenet.mixin.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BellBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(BellBlockEntity.class)
public abstract class MixinBellBlockEntity {

    @Shadow
    protected static boolean isRaiderWithinRange(BlockPos p_155197_, LivingEntity p_155198_) {
        return false;
    }

    @Shadow
    protected static void glow(LivingEntity p_58841_) {
    }

    /**
     * @author wdog5
     * @reason bukkit
     */
    @Overwrite
    private static void makeRaidersGlow(Level p_155187_, BlockPos p_155188_, List<LivingEntity> p_155189_) {
        List<org.bukkit.entity.LivingEntity> entities = // CraftBukkit
        p_155189_.stream().filter(p_155219_ -> isRaiderWithinRange(p_155188_, p_155219_)).map((entity) -> (org.bukkit.entity.LivingEntity) entity.getBukkitEntity()).collect(java.util.stream.Collectors.toCollection(java.util.ArrayList::new)); // CraftBukkit
        org.bukkit.craftbukkit.event.CraftEventFactory.handleBellResonateEvent(p_155187_, p_155188_, entities).forEach(MixinBellBlockEntity::glow0);
        // CraftBukkit end
    }

    private static void glow0(LivingEntity p_58841_) {
        glow(p_58841_);
    }

}
