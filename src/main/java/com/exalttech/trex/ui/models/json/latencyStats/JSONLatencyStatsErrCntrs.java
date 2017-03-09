package com.exalttech.trex.ui.models.json.latencyStats;


public class JSONLatencyStatsErrCntrs {
    int out_of_order;
    int seq_too_high;
    int dropped;
    int seq_too_low;
    int dup;

    public int getOut_of_order() {
        return out_of_order;
    }
    public void setOut_of_order(int out_of_order) {
        this.out_of_order = out_of_order;
    }

    public int getSeq_too_high() {
        return seq_too_high;
    }
    public void setSeq_too_high(int seq_too_high) {
        this.seq_too_high = seq_too_high;
    }

    public int getDropped() {
        return dropped;
    }
    public void setDropped(int dropped) {
        this.dropped = dropped;
    }

    public int getSeq_too_low() {
        return seq_too_low;
    }
    public void setSeq_too_low(int seq_too_low) {
        this.seq_too_low = seq_too_low;
    }

    public int getDup() {
        return dup;
    }
    public void setDup(int dup) {
        this.dup = dup;
    }
}
