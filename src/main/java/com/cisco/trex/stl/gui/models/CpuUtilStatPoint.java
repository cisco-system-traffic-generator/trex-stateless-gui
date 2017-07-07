package com.cisco.trex.stl.gui.models;

public class CpuUtilStatPoint {
    private Integer value;
    private double time;

    public CpuUtilStatPoint(Integer value, double time) {
        this.value = value;
        this.time = time;
    }

    public Number getValue() {
        return value;
    }

    public double getTime() {
        return time;
    }
}
