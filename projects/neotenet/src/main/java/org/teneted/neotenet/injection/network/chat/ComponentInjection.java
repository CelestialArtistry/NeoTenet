package org.teneted.neotenet.injection.network.chat;

import net.minecraft.network.chat.Component;

import java.util.Iterator;
import java.util.stream.Stream;

public interface ComponentInjection extends Iterable<Component> {

    default Stream<Component> stream() {
        throw new IllegalArgumentException("Not implemented");
    }

    @Override
    default Iterator<Component> iterator() {
        throw new IllegalArgumentException("Not implemented");
    }
}