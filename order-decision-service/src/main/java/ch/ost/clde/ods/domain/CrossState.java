package ch.ost.clde.ods.domain;

import java.util.function.BiConsumer;

public enum CrossState {
    ABOVE, BELOW, NONE;

    public void ifChangedFrom(CrossState lastState, BiConsumer<CrossState, CrossState> action) {
        if (this != lastState) action.accept(lastState, this);
    }
}