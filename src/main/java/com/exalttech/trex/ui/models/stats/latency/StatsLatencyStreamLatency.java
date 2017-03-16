package com.exalttech.trex.ui.models.stats.latency;

import java.util.HashMap;
import java.util.Map;


public class StatsLatencyStreamLatency {
    Map<String, Integer> histogram = new HashMap<>();
    double average = 0.0;
    int jitter = 0;
    int lastMax = 0;
    int totalMax = 0;

    public Map<String, Integer> getHistogram() {
        return histogram;
    }

    public double getAverage() {
        return average;
    }
    public void setAverage(double average) {
        this.average = average;
    }

    public int getJitter() {
        return jitter;
    }
    public void setJitter(int jitter) {
        this.jitter = jitter;
    }

    public int getLastMax() {
        return lastMax;
    }
    public void setLastMax(int lastMax) {
        this.lastMax = lastMax;
    }

    public int getTotalMax() {
        return totalMax;
    }
    public void setTotalMax(int totalMax) {
        this.totalMax = totalMax;
    }
}
