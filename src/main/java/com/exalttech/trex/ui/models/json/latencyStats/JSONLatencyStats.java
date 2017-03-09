package com.exalttech.trex.ui.models.json.latencyStats;


public class JSONLatencyStats {
    private JSONLatencyStatsLatency latency;
    private JSONLatencyStatsErrCntrs err_cntrs;

    public JSONLatencyStatsLatency getLatency() {
        return latency;
    }
    public void setLatency(JSONLatencyStatsLatency latency) {
        this.latency = latency;
    }

    public JSONLatencyStatsErrCntrs getErr_cntrs() {
        return err_cntrs;
    }
    public void setErr_cntrs(JSONLatencyStatsErrCntrs err_cntrs) {
        this.err_cntrs = err_cntrs;
    }
}
