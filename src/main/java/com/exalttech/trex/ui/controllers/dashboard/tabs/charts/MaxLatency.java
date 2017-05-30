package com.exalttech.trex.ui.controllers.dashboard.tabs.charts;

import javafx.beans.property.IntegerProperty;

import com.exalttech.trex.ui.models.stats.LatencyStatPoint;


public class MaxLatency extends LatencyLineChart {
    public MaxLatency(final IntegerProperty interval) {
        super(interval);
    }

    protected String getYChartLabel() {
        return "Max Latency (\u00B5s)";
    }

    @Override
    protected Number getValue(final LatencyStatPoint point) {
        return point.getLatencyStat().getLat().getTotalMax();
    }
}
