package com.exalttech.trex.ui.models.stats.latency;

public class LatencyInfo {
    private double average = 0.0;
    private int totalMax = 0;
    private int jitter = 0;
    private int dropped = 0;
    private int dup = 0;
    private int outOfOrder = 0;
    private int seqTooHigh = 0;
    private int seqTooLow = 0;

    public LatencyInfo(final LatencyInfo latencyInfo) {
        this.average = latencyInfo.average;
        this.totalMax = latencyInfo.totalMax;
        this.jitter = latencyInfo.jitter;
        this.dropped = latencyInfo.dropped;
        this.dup = latencyInfo.dup;
        this.outOfOrder = latencyInfo.outOfOrder;
        this.seqTooHigh = latencyInfo.seqTooHigh;
        this.seqTooLow = latencyInfo.seqTooLow;
    }

    public LatencyInfo(final LatencyStatsLatency latency, final LatencyStastsErrCntrs errCntrs) {
        if (latency != null) {
            this.average = latency.getAverage();
            this.totalMax = latency.getTotalMax();
            this.jitter = latency.getJitter();
        }
        if (errCntrs != null) {
            this.dropped = errCntrs.getDropped();
            this.dup = errCntrs.getDup();
            this.outOfOrder = errCntrs.getOutOfOrder();
            this.seqTooHigh = errCntrs.getSeqTooHigh();
            this.seqTooLow = errCntrs.getSeqTooLow();
        }
    }

    public double getAverage() {
        return average;
    }

    public int getTotalMax() {
        return totalMax;
    }

    public int getJitter() {
        return jitter;
    }

    public int getDropped() {
        return dropped;
    }

    public int getDup() {
        return dup;
    }

    public int getOutOfOrder() {
        return outOfOrder;
    }

    public int getSeqTooHigh() {
        return seqTooHigh;
    }

    public int getSeqTooLow() {
        return seqTooLow;
    }

    public int getTotalErrors() {
        return dropped + dup + outOfOrder + seqTooHigh + seqTooLow;
    }

    public void plusShadow(final LatencyInfo shadow) {
        this.dropped += shadow.dropped;
        this.dup += shadow.dup;
        this.outOfOrder += shadow.outOfOrder;
        this.seqTooHigh += shadow.seqTooHigh;
        this.seqTooLow += shadow.seqTooLow;
    }

    public void minusShadow(final LatencyInfo shadow) {
        this.dropped -= shadow.dropped;
        this.dup -= shadow.dup;
        this.outOfOrder -= shadow.outOfOrder;
        this.seqTooHigh -= shadow.seqTooHigh;
        this.seqTooLow -= shadow.seqTooLow;
    }

    public void resetShadow() {
        this.dropped = 0;
        this.dup = 0;
        this.outOfOrder = 0;
        this.seqTooHigh = 0;
        this.seqTooLow = 0;
    }
}
