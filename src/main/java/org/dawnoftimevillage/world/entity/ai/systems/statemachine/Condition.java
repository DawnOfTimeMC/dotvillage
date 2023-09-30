package org.dawnoftimevillage.world.entity.ai.systems.statemachine;

@FunctionalInterface
public interface Condition {
    boolean validate();
}
