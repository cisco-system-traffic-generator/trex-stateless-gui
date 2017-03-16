package com.exalttech.trex.ui.models.stats.latency;


public class StatsLatencyStream {
    private StatsLatencyStreamLatency latency;
    private StatsLatencyStreamErrCntrs errCntrs;

    public StatsLatencyStream(StatsLatencyStreamLatency latency, StatsLatencyStreamErrCntrs errCntrs) {
        this.latency = latency;
        this.errCntrs = errCntrs;
    }

    public StatsLatencyStreamLatency getLatency() {
        return latency;
    }

    public StatsLatencyStreamErrCntrs getErrCntrs() {
        return errCntrs;
    }
}
