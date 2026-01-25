package org.teneted.neotenet.eventhandler;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FungusBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockGrowFeatureEvent;
import net.neoforged.neoforge.internal.versions.neoforge.NeoForgeVersion;

@EventBusSubscriber(modid = NeoForgeVersion.MOD_ID)
public class BlockEventDispatcher {

    @SubscribeEvent
    public static void onFungusBlockGrow(BlockGrowFeatureEvent event) {
        if (event.getLevel().getBlockState(event.getPos()).getBlock() instanceof FungusBlock fungusBlock) {
            // CraftBukkit start
            if (fungusBlock == Blocks.WARPED_FUNGUS) {
                SaplingBlock.treeType = org.bukkit.TreeType.WARPED_FUNGUS;
            } else if (fungusBlock == Blocks.CRIMSON_FUNGUS) {
                SaplingBlock.treeType = org.bukkit.TreeType.CRIMSON_FUNGUS;
            }
            // CraftBukkit end
        }
    }
}
