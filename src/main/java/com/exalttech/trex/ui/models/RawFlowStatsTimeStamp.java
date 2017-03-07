package com.exalttech.trex.ui.models;


public class RawFlowStatsTimeStamp {
    Long freq;
    Long value;

    public RawFlowStatsTimeStamp() {}

    public RawFlowStatsTimeStamp(Long freq, Long value) {
        this.freq = freq;
        this.value = value;
    }

    public Long getFreq() {
        return freq;
    }

    public void setFreq(Long freq) {
        this.freq = freq;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }
}
