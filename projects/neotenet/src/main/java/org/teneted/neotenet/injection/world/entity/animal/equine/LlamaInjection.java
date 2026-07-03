package org.teneted.neotenet.injection.world.entity.animal.equine;

public interface LlamaInjection {

    default void setStrengthPublic(int strength) {
        throw new IllegalArgumentException("Not implemented");
    }
}
