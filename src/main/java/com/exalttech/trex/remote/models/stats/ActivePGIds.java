package com.exalttech.trex.remote.models.stats;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivePGIds {
    @JsonProperty("flow_stats")
    private Integer[] flowStats;
    @JsonProperty("latency")
    private Integer[] latency;

    @JsonProperty("flow_stats")
    public Integer[] getFlowStats() {
        return flowStats;
    }

    @JsonProperty("flow_stats")
    public void setFlowStats(final Integer[] flowStats) {
        this.flowStats = flowStats;
    }

    @JsonProperty("latency")
    public Integer[] getLatency() {
        return latency;
    }

    @JsonProperty("latency")
    public void setLatency(final Integer[] latency) {
        this.latency = latency;
    }
}
