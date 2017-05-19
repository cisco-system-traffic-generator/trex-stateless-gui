package com.exalttech.trex.remote.models.stats;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;


@JsonIgnoreProperties(ignoreUnknown = true)
public class PGIdStatsRPCResult {
    @JsonProperty("flow_stats")
    private Map<String, FlowStat> flowStats;
    @JsonProperty("latency")
    private Map<String, LatencyStat> latency;

    @JsonProperty("flow_stats")
    public Map<String, FlowStat> getFlowStats() {
        return flowStats;
    }

    @JsonProperty("flow_stats")
    public void setFlowStats(final Map<String, FlowStat> flowStats) {
        this.flowStats = flowStats;
    }

    @JsonProperty("latency")
    public Map<String, LatencyStat> getLatency() {
        return latency;
    }

    @JsonProperty("latency")
    public void setLatency(final Map<String, LatencyStat> latency) {
        this.latency = latency;
    }
}
