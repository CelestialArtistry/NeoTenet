package org.celestial_artistry.neotenet.mixin.server.players;

import java.util.Collection;
import java.util.Map;
import net.minecraft.server.players.StoredUserEntry;
import net.minecraft.server.players.StoredUserList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.celestial_artistry.neotenet.injection.server.players.InjectionStoredUserList;

@Mixin(StoredUserList.class)
public abstract class MixinStoredUserList<K, V extends StoredUserEntry<K>> implements InjectionStoredUserList<K, V> {

    @Shadow
    @Final
    private Map<String, V> map;

    @Override
    public Collection<V> getValues() {
        return this.map.values();
    }
}
