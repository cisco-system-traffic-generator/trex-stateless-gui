package com.exalttech.trex.ui.models.stats.latency;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class LatencyStastsErrCntrs {
    @JsonProperty("dropped")
    private int dropped = 0;
    @JsonProperty("dup")
    private int dup = 0;
    @JsonProperty("out_of_order")
    private int outOfOrder = 0;
    @JsonProperty("seq_to_high")
    private int seqTooHigh = 0;
    @JsonProperty("seq_to_low")
    private int seqTooLow = 0;

    @JsonProperty("dropped")
    public int getDropped() {
        return dropped;
    }

    @JsonProperty("dropped")
    public void setDropped(final int dropped) {
        this.dropped = dropped;
    }

    @JsonProperty("dup")
    public int getDup() {
        return dup;
    }

    @JsonProperty("dup")
    public void setDup(final int dup) {
        this.dup = dup;
    }

    @JsonProperty("out_of_order")
    public int getOutOfOrder() {
        return outOfOrder;
    }

    @JsonProperty("out_of_order")
    public void setOutOfOrder(final int outOfOrder) {
        this.outOfOrder = outOfOrder;
    }

    @JsonProperty("seq_to_high")
    public int getSeqTooHigh() {
        return seqTooHigh;
    }

    @JsonProperty("seq_to_high")
    public void setSeqTooHigh(final int seqTooHigh) {
        this.seqTooHigh = seqTooHigh;
    }

    @JsonProperty("seq_to_low")
    public int getSeqTooLow() {
        return seqTooLow;
    }

    @JsonProperty("seq_to_low")
    public void setSeqTooLow(final int seqTooLow) {
        this.seqTooLow = seqTooLow;
    }
}
