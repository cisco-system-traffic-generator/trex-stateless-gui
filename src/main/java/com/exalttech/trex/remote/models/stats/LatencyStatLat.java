package com.exalttech.trex.remote.models.stats;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;


@JsonIgnoreProperties(ignoreUnknown = true)
public class LatencyStatLat {
    @JsonProperty("average")
    private double average;
    @JsonProperty("histogram")
    private Map<String, Long> histogram;
    @JsonProperty("jit")
    private long jit;
    @JsonProperty("last_max")
    private long lastMax;
    @JsonProperty("total_max")
    private long totalMax;

    @JsonProperty("average")
    public double getAverage() {
        return average;
    }

    @JsonProperty("average")
    public void setAverage(final double average) {
        this.average = average;
    }

    @JsonProperty("histogram")
    public Map<String, Long> getHistogram() {
        return histogram;
    }

    @JsonProperty("histogram")
    public void setHistogram(final Map<String, Long> histogram) {
        this.histogram = histogram;
    }

    @JsonProperty("jit")
    public long getJit() {
        return jit;
    }

    @JsonProperty("jit")
    public void setJit(final long jit) {
        this.jit = jit;
    }

    @JsonProperty("last_max")
    public long getLastMax() {
        return lastMax;
    }

    @JsonProperty("last_max")
    public void setLastMax(final long lastMax) {
        this.lastMax = lastMax;
    }

    @JsonProperty("total_max")
    public long getTotalMax() {
        return totalMax;
    }

    @JsonProperty("total_max")
    public void setTotalMax(final long totalMax) {
        this.totalMax = totalMax;
    }
}
