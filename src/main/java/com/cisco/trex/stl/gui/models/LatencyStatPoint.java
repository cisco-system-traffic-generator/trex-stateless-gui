package com.cisco.trex.stl.gui.models;

import com.cisco.trex.stateless.model.stats.LatencyStat;


public class LatencyStatPoint {
    private LatencyStat latencyStat;
    private double time;

    public LatencyStatPoint(final LatencyStat latencyStat, final double time) {
        this.latencyStat = latencyStat;
        this.time = time;
    }

    public LatencyStat getLatencyStat() {
        return this.latencyStat;
    }

    public double getTime() {
        return time;
    }
}
