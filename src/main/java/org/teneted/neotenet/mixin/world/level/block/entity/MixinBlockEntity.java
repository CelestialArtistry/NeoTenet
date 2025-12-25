package org.teneted.neotenet.mixin.world.level.block.entity;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.bukkit.craftbukkit.persistence.CraftPersistentDataContainer;
import org.bukkit.craftbukkit.persistence.CraftPersistentDataTypeRegistry;
import org.bukkit.inventory.InventoryHolder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.teneted.neotenet.injection.world.level.block.entity.InjectionBlockEntity;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

@Mixin(BlockEntity.class)
public abstract class MixinBlockEntity implements InjectionBlockEntity {

    @Shadow
    public CraftPersistentDataContainer persistentDataContainer;

    @Shadow
    @Final
    private static CraftPersistentDataTypeRegistry DATA_TYPE_REGISTRY;

    @Shadow
    protected abstract void applyImplicitComponents(BlockEntity.DataComponentInput p_338718_);

    @Shadow
    private DataComponentMap components;

    @Shadow
    @Nullable
    protected Level level;

    @Shadow
    @Final
    protected BlockPos worldPosition;

    @Inject(method = "loadAdditional", at = @At("HEAD"))
    private void neotenet$addBukkitInfo(CompoundTag p_338466_, HolderLookup.Provider p_338445_, CallbackInfo ci) {
        this.persistentDataContainer = new CraftPersistentDataContainer(DATA_TYPE_REGISTRY);

        net.minecraft.nbt.Tag persistentDataTag = p_338466_.get("PublicBukkitValues");
        if (persistentDataTag instanceof CompoundTag) {
            this.persistentDataContainer.putAll((CompoundTag) persistentDataTag);
        }
    }

    @Inject(method = "loadWithComponents", at = @At("RETURN"))
    private void neotenet$storeContainer(CompoundTag p_338356_, HolderLookup.Provider p_338558_, CallbackInfo ci) {
        // CraftBukkit start - store container
        if (this.persistentDataContainer != null && !this.persistentDataContainer.isEmpty()) {
            p_338356_.put("PublicBukkitValues", this.persistentDataContainer.toTagCompound());
        }
        // CraftBukkit end
    }

    @Inject(method = "applyComponents", at = @At("RETURN"))
    private void neotenet$removeComponents(DataComponentMap p_330364_, DataComponentPatch p_338381_, CallbackInfo ci, @Local Set<DataComponentType<?>> set) {
        set.remove(DataComponents.BLOCK_ENTITY_DATA); // Remove as never actually added by applyImplicitComponents
    }

    @Override
    public Set<DataComponentType<?>> applyComponentsSet(DataComponentMap p_330364_, DataComponentPatch p_338381_) {
        final Set<DataComponentType<?>> set = new HashSet<>();
        set.add(DataComponents.BLOCK_ENTITY_DATA);
        final DataComponentMap datacomponentmap = PatchedDataComponentMap.fromPatch(p_330364_, p_338381_);
        this.applyImplicitComponents(new BlockEntity.DataComponentInput() {
            @Nullable
            @Override
            public <T> T get(DataComponentType<T> p_338266_) {
                set.add(p_338266_);
                return datacomponentmap.get(p_338266_);
            }

            @Override
            public <T> T getOrDefault(DataComponentType<? extends T> p_338358_, T p_338352_) {
                set.add(p_338358_);
                return datacomponentmap.getOrDefault(p_338358_, p_338352_);
            }
        });
        DataComponentPatch datacomponentpatch = p_338381_.forget(set::contains);
        this.components = datacomponentpatch.split().added();
        // CraftBukkit start
        set.remove(DataComponents.BLOCK_ENTITY_DATA); // Remove as never actually added by applyImplicitComponents
        return set;
        // CraftBukkit end
    }

    // CraftBukkit start - add method
    @Override
    public InventoryHolder getOwner() {
        if (level == null) return null;
        org.bukkit.block.BlockState state = level.getWorld().getBlockAt(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ()).getState();
        if (state instanceof InventoryHolder) return (InventoryHolder) state;
        return null;
    }
    // CraftBukkit end
}
