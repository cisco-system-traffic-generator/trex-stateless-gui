package com.exalttech.trex.ui.controllers.dashboard.tabs.charts;

import javafx.beans.property.IntegerProperty;


public class DashboardTabChartsFactory {
    public static class ChartTypes {
        public static final String TX_PKTS = "Tx (pkt)";
        public static final String RX_PKTS = "Rx (pkt)";
        public static final String TX_BYTES = "Tx (B)";
        public static final String RX_BYTES = "Rx (B)";
        public static final String TX_PKTS_PER_SECOND = "Tx (pkt/s)";
        public static final String RX_PKTS_PER_SECOND = "Rx (pkt/s)";
        public static final String TX_BYTES_PER_SECOND_L1 = "Tx L1 (B/s)";
        public static final String TX_BYTES_PER_SECOND_L2 = "Tx L2 (B/s)";
        public static final String RX_BYTES_PER_SECOND = "Rx (B/s)";
        public static final String MAX_LATENCY = "Max Latency (ms)";
        public static final String LATENCY_HISTOGRAM = "Latency Histogram";
    }

    public static DashboardTabChartsUpdatable create(String chartType, IntegerProperty interval) {
        switch (chartType) {
            case ChartTypes.TX_PKTS:
                return new DashboardTabChartsTxPkts(interval);
            case ChartTypes.RX_PKTS:
                return new DashboardTabChartsRxPkts(interval);
            case ChartTypes.TX_BYTES:
                return new DashboardTabChartsTxBytes(interval);
            case ChartTypes.RX_BYTES:
                return new DashboardTabChartsRxBytes(interval);
            case ChartTypes.TX_PKTS_PER_SECOND:
                return new DashboardTabChartsTxPps(interval);
            case ChartTypes.RX_PKTS_PER_SECOND:
                return new DashboardTabChartsRxPps(interval);
            case ChartTypes.TX_BYTES_PER_SECOND_L1:
                return new DashboardTabChartsTxBpsL1(interval);
            case ChartTypes.TX_BYTES_PER_SECOND_L2:
                return new DashboardTabChartsTxBpsL2(interval);
            case ChartTypes.RX_BYTES_PER_SECOND:
                return new DashboardTabChartsRxBps(interval);
            case ChartTypes.MAX_LATENCY:
                return new DashboardTabChartsMaxLatency(interval);
            case ChartTypes.LATENCY_HISTOGRAM:
                return new DashboardTabChartsLatencyHistogram();
        }
        throw new IllegalArgumentException(String.format("Unknown chart type: %s", chartType));
    }
}
