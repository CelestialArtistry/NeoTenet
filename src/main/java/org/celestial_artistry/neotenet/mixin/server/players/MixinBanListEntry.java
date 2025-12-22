package org.celestial_artistry.neotenet.mixin.server.players;

import java.util.Date;
import net.minecraft.server.players.BanListEntry;
import net.minecraft.server.players.StoredUserEntry;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.celestial_artistry.neotenet.injection.server.players.InjectionBanListEntry;

@Mixin(BanListEntry.class)
public abstract class MixinBanListEntry<T> extends StoredUserEntry<T> implements InjectionBanListEntry {

    // @formatter:off
    @Shadow @Final protected Date created;

    public MixinBanListEntry(@Nullable T object) {
        super(object);
    }
    // @formatter:on

    @Override
    public Date getCreated() {
        return this.created;
    }
}
