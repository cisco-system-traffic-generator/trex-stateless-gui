package com.exalttech.trex.ui.models;

public enum FlowControl {
    NONE(0), TX(1), RX(2), FULL(3);

    private final int mode;

    FlowControl(int mode) {
        this.mode = mode;
    }

    public int getVal() {
        return mode;
    }
}
