package com.exalttech.trex.ui.models.stats.latency;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class LatencyStatsStream {
    @JsonProperty("latency")
    private LatencyStatsLatency latency = new LatencyStatsLatency();
    @JsonProperty("err_cntrs")
    private LatencyStastsErrCntrs errCntrs = new LatencyStastsErrCntrs();

    @JsonProperty("latency")
    public LatencyStatsLatency getLatency() {
        return latency;
    }

    @JsonProperty("latency")
    public void setLatency(final LatencyStatsLatency latency) {
        this.latency = latency;
    }

    @JsonProperty("err_cntrs")
    public LatencyStastsErrCntrs getErrCntrs() {
        return errCntrs;
    }

    @JsonProperty("err_cntrs")
    public void setErrCntrs(final LatencyStastsErrCntrs errCntrs) {
        this.errCntrs = errCntrs;
    }
}
