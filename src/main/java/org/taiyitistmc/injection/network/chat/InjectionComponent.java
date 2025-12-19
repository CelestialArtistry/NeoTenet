package org.taiyitistmc.injection.network.chat;

import java.util.stream.Stream;
import net.minecraft.network.chat.Component;

public interface InjectionComponent {

    default Stream<Component> stream() {
        throw new IllegalStateException("Not implemented");
    }
}
