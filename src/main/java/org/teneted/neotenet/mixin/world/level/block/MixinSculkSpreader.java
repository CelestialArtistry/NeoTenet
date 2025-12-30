package org.teneted.neotenet.mixin.world.level.block;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SculkSpreader;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.event.block.SculkBloomEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SculkSpreader.class)
public abstract class MixinSculkSpreader {


    @Shadow
    public abstract boolean isWorldGeneration();

    @Shadow
    public Level level;

    @Inject(method = "addCursor", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"), cancellable = true)
    private void neotenet$callSculkBloomEvent(SculkSpreader.ChargeCursor p_222261_, CallbackInfo ci) {
        // CraftBukkit start
        if (!isWorldGeneration()) { // CraftBukkit - SPIGOT-7475: Don't call event during world generation
            CraftBlock bukkitBlock = CraftBlock.at(level, p_222261_.pos);
            SculkBloomEvent event = new SculkBloomEvent(bukkitBlock, p_222261_.getCharge());
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                ci.cancel();
                return;
            }

            p_222261_.charge = event.getCharge();
        }
        // CraftBukkit end
    }
}
