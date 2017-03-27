package com.exalttech.trex.ui.models.stats.latency;


public class StatsLatencyStreamErrCntrs {
    int outOfOrder = 0;
    int seqTooHigh = 0;
    int dropped = 0;
    int seqTooLow = 0;
    int dup = 0;

    public int getOutOfOrder() {
        return outOfOrder;
    }
    public void setOutOfOrder(int outOfOrder) {
        this.outOfOrder = outOfOrder;
    }

    public int getSeqTooHigh() {
        return seqTooHigh;
    }
    public void setSeqTooHigh(int seqTooHigh) {
        this.seqTooHigh = seqTooHigh;
    }

    public int getDropped() {
        return dropped;
    }
    public void setDropped(int dropped) {
        this.dropped = dropped;
    }

    public int getSeqTooLow() {
        return seqTooLow;
    }
    public void setSeqTooLow(int seqTooLow) {
        this.seqTooLow = seqTooLow;
    }

    public int getDup() {
        return dup;
    }
    public void setDup(int dup) {
        this.dup = dup;
    }

    public int getTotal() {
        return outOfOrder + seqTooHigh + dropped + seqTooLow + dup;
    }
}
