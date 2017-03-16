package com.exalttech.trex.ui.models.stats.latency;

public class MaxLatencyPoint {
    private int value;
    private double time;

    public MaxLatencyPoint(int value, double time) {
        this.value = value;
        this.time = time;
    }

    public int getValue() {
        return value;
    }

    public double getTime() {
        return time;
    }
}
