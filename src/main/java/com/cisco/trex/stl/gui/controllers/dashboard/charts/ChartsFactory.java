package com.cisco.trex.stl.gui.controllers.dashboard.charts;

import javafx.beans.property.IntegerProperty;


public class ChartsFactory {
    public static class ChartTypes {
        public static final String TX_PPS = "Tx pps";
        public static final String RX_PPS = "Rx pps";
        public static final String TX_BPS_L1 = "Tx bps L1";
        public static final String TX_BPS_L2 = "Tx bps L2";
        public static final String RX_BPS_L2 = "Rx bps L2";
        public static final String MAX_LATENCY = "Max Latency";
        public static final String AVG_LATENCY = "Avg Latency";
        public static final String JITTER_LATENCY = "Jitter Latency";
        public static final String TEMPORARY_MAX_LATENCY = "Temporary Max Latency";
        public static final String LATENCY_HISTOGRAM = "Latency Histogram";
    }

    public static FlowChart create(String chartType, IntegerProperty interval) {
        switch (chartType) {
            case ChartTypes.TX_PPS:
                return new TxPps(interval);
            case ChartTypes.RX_PPS:
                return new RxPps(interval);
            case ChartTypes.TX_BPS_L1:
                return new TxBpsL1(interval);
            case ChartTypes.TX_BPS_L2:
                return new TxBpsL2(interval);
            case ChartTypes.RX_BPS_L2:
                return new RxBps(interval);
            case ChartTypes.MAX_LATENCY:
                return new MaxLatency(interval);
            case ChartTypes.AVG_LATENCY:
                return new AvgLatency(interval);
            case ChartTypes.JITTER_LATENCY:
                return new LatencyJitter(interval);
            case ChartTypes.TEMPORARY_MAX_LATENCY:
                return new TemporaryMaxLatency(interval);
            case ChartTypes.LATENCY_HISTOGRAM:
                return new LatencyHistogram();
        }
        throw new IllegalArgumentException(String.format("Unknown chart type: %s", chartType));
    }
}
