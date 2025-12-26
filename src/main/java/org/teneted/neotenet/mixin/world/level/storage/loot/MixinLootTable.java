package org.teneted.neotenet.mixin.world.level.storage.loot;

import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.world.LootGenerateEvent;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.teneted.neotenet.injection.world.level.storage.loot.InjectionLootTable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(LootTable.class)
public abstract class MixinLootTable implements InjectionLootTable {

    @Shadow
    @Final
    private static Logger LOGGER;

    @Shadow
    protected abstract List<Integer> getAvailableSlots(Container p_230920_, RandomSource p_230921_);

    @Shadow
    protected abstract ObjectArrayList<ItemStack> getRandomItems(LootContext p_230923_);

    @Shadow
    @Final
    private Optional<ResourceLocation> randomSequence;

    @Shadow
    protected abstract void shuffleAndSplitItems(ObjectArrayList<ItemStack> p_230925_, int p_230926_, RandomSource p_230927_);

    @Unique
    private AtomicBoolean neotenet$plugin = new AtomicBoolean(false);

    @Inject(method = "fill", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/loot/LootTable;getAvailableSlots(Lnet/minecraft/world/Container;Lnet/minecraft/util/RandomSource;)Ljava/util/List;"))
    private void neotenet$callLootGenerateEvent(Container p_287662_, LootParams p_287743_, long p_287585_, CallbackInfo ci, @Local(ordinal = 0) ObjectArrayList<ItemStack> objectarraylist, @Local(ordinal = 0) LootContext lootcontext) {
        LootGenerateEvent event = CraftEventFactory.callLootGenerateEvent(p_287662_, ((LootTable) (Object) this), lootcontext, objectarraylist, neotenet$plugin.get());
        if (event.isCancelled()) {
            return;
        }
        objectarraylist = event.getLoot().stream().map(CraftItemStack::asNMSCopy).collect(ObjectArrayList.toList());
        neotenet$plugin.getAndSet(false);
        // CraftBukkit end
    }

    public void fillInventory(Container iinventory, LootParams lootparams, long i, boolean plugin) {
        LootContext lootcontext = new LootContext.Builder(lootparams).withOptionalRandomSeed(i).create(this.randomSequence);
        ObjectArrayList<ItemStack> objectarraylist = this.getRandomItems(lootcontext);
        RandomSource randomsource = lootcontext.getRandom();
        // CraftBukkit start
        neotenet$plugin.set(plugin);
        LootGenerateEvent event = CraftEventFactory.callLootGenerateEvent(iinventory, ((LootTable) (Object) this), lootcontext, objectarraylist, plugin);
        if (event.isCancelled()) {
            return;
        }
        objectarraylist = event.getLoot().stream().map(CraftItemStack::asNMSCopy).collect(ObjectArrayList.toList());
        // CraftBukkit end
        List<Integer> list = this.getAvailableSlots(iinventory, randomsource);
        this.shuffleAndSplitItems(objectarraylist, list.size(), randomsource);

        for (ItemStack itemstack : objectarraylist) {
            if (list.isEmpty()) {
                LOGGER.warn("Tried to over-fill a container");
                return;
            }
            if (itemstack.isEmpty()) {
                iinventory.setItem(list.remove(list.size() - 1), ItemStack.EMPTY);
            } else {
                iinventory.setItem(list.remove(list.size() - 1), itemstack);
            }
        }
    }
}
