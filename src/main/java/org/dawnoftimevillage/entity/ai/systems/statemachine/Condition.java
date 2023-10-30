package org.dawnoftimevillage.entity.ai.systems.statemachine;

@FunctionalInterface
public interface Condition {
    boolean validate();
}
