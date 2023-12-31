package org.dotvill.entity.ai.systems.statemachine;

@FunctionalInterface
public interface Condition {
    boolean validate();
}
