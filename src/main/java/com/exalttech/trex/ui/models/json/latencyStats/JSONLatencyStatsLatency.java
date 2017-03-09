package com.exalttech.trex.ui.models.json.latencyStats;

import java.util.Map;


public class JSONLatencyStatsLatency {
    Map<String, Integer> histogram;
    double average;
    int jitter;
    int last_max;
    int total_max;

    public Map<String, Integer> getHistogram() {
        return histogram;
    }
    public void setHistogram(Map<String, Integer> histogram) {
        this.histogram = histogram;
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

    public int getLast_max() {
        return last_max;
    }
    public void setLast_max(int last_max) {
        this.last_max = last_max;
    }

    public int getTotal_max() {
        return total_max;
    }
    public void setTotal_max(int total_max) {
        this.total_max = total_max;
    }
}
