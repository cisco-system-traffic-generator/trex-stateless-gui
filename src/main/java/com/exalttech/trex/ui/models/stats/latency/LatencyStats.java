package com.exalttech.trex.ui.models.stats.latency;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class LatencyStats {
    @JsonProperty("data")
    private Map<String, LatencyStatsStream> data = new HashMap<>();

    @JsonProperty("data")
    public Map<String, LatencyStatsStream> getData() {
        return data;
    }

    @JsonProperty("data")
    public void setData(final Map<String, LatencyStatsStream> data) {
        this.data = data;
    }
}
