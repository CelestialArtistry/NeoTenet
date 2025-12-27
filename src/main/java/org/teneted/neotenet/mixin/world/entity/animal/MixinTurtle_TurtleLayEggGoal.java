package org.teneted.neotenet.mixin.world.entity.animal;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TurtleEggBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(targets = "net.minecraft.world.entity.animal.Turtle.TurtleLayEggGoal")
public abstract class MixinTurtle_TurtleLayEggGoal extends MoveToBlockGoal {

    @Shadow
    @Final
    private Turtle turtle;

    public MixinTurtle_TurtleLayEggGoal(PathfinderMob p_25609_, double p_25610_, int p_25611_) {
        super(p_25609_, p_25610_, p_25611_);
    }

    /**
     * @author wdog5
     * @reason bukkit
     */
    @Overwrite
    public void tick() {
        super.tick();
        BlockPos blockpos = this.turtle.blockPosition();
        if (!this.turtle.isInWater() && this.isReachedTarget()) {
            if (this.turtle.layEggCounter < 1) {
                this.turtle.setLayingEgg(true);
            } else if (this.turtle.layEggCounter > this.adjustedTickDelay(200)) {
                Level level = this.turtle.level();
                if (org.bukkit.craftbukkit.event.CraftEventFactory.callEntityChangeBlockEvent(this.turtle, this.blockPos.above(), (BlockState) Blocks.TURTLE_EGG.defaultBlockState().setValue(TurtleEggBlock.EGGS, this.turtle.random.nextInt(4) + 1))) { // CraftBukkit
                    level.playSound(null, blockpos, SoundEvents.TURTLE_LAY_EGG, SoundSource.BLOCKS, 0.3F, 0.9F + level.random.nextFloat() * 0.2F);
                    BlockPos blockpos1 = this.blockPos.above();
                    BlockState blockstate = Blocks.TURTLE_EGG
                            .defaultBlockState()
                            .setValue(TurtleEggBlock.EGGS, Integer.valueOf(this.turtle.random.nextInt(4) + 1));
                    level.setBlock(blockpos1, blockstate, 3);
                    level.gameEvent(GameEvent.BLOCK_PLACE, blockpos1, GameEvent.Context.of(this.turtle, blockstate));
                }
                this.turtle.setHasEgg(false);
                this.turtle.setLayingEgg(false);
                this.turtle.setInLoveTime(600);
            }

            if (this.turtle.isLayingEgg()) {
                this.turtle.layEggCounter++;
            }
        }
    }
}
