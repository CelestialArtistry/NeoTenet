package org.celestial_artistry.neotenet.mixin.server.players;

import com.mojang.authlib.GameProfile;
import java.io.File;
import net.minecraft.server.players.StoredUserList;
import net.minecraft.server.players.UserWhiteList;
import net.minecraft.server.players.UserWhiteListEntry;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(UserWhiteList.class)
public abstract class MixinUserWhiteList extends StoredUserList<GameProfile, UserWhiteListEntry> {

    public MixinUserWhiteList(File file) {
        super(file);
    }

    // Paper start - Add whitelist events
    @Override
    public void add(UserWhiteListEntry entry) {
        super.add(entry);
    }

    @Override
    public void remove(GameProfile profile) {
        super.remove(profile);
    }
    // Paper end
}
