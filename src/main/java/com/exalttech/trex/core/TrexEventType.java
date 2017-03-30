package com.exalttech.trex.core;

public enum TrexEventType {
    PORT_STARTED(0), 
    PORT_STOPPED(1), 
    PORT_PAUSED(2), 
    PORT_RESUMED(3), 
    PORT_FINISHED_TX(4), 
    PORT_ACQUIRED(5), 
    PORT_RELEASED(6), 
    PORT_ERROR(7), 
    PORT_ATTR_CHANGED(8),
    SERVER_STOPPED(100),
    UNKNOWN_TYPE(-1);

    private int value;

    TrexEventType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
