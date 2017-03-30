package com.exalttech.trex.ui.models.stats.latency;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class LatencyStatsLatency {
    @JsonProperty("histogram")
    private Map<String, Long> histogram = new HashMap<>();
    @JsonProperty("average")
    private double average = 0.0;
    @JsonProperty("jitter")
    private int jitter = 0;
    @JsonProperty("last_max")
    private int lastMax = 0;
    @JsonProperty("total_max")
    private int totalMax = 0;

    @JsonProperty("histogram")
    public Map<String, Long> getHistogram() {
        return histogram;
    }

    @JsonProperty("histogram")
    public void setHistogram(final Map<String, Long> histogram) {
        this.histogram = histogram;
    }

    @JsonProperty("average")
    public double getAverage() {
        return average;
    }

    @JsonProperty("average")
    public void setAverage(final double average) {
        this.average = average;
    }

    @JsonProperty("jitter")
    public int getJitter() {
        return jitter;
    }

    @JsonProperty("jitter")
    public void setJitter(final int jitter) {
        this.jitter = jitter;
    }

    @JsonProperty("last_max")
    public int getLastMax() {
        return lastMax;
    }

    @JsonProperty("last_max")
    public void setLastMax(final int lastMax) {
        this.lastMax = lastMax;
    }

    @JsonProperty("total_max")
    public int getTotalMax() {
        return totalMax;
    }

    @JsonProperty("total_max")
    public void setTotalMax(final int totalMax) {
        this.totalMax = totalMax;
    }
}
