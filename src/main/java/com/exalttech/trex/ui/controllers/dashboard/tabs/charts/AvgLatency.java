package com.exalttech.trex.ui.controllers.dashboard.tabs.charts;

import javafx.beans.property.IntegerProperty;

import com.cisco.trex.stateless.gui.models.LatencyStatPoint;


public class AvgLatency extends LatencyLineChart {
    public AvgLatency(final IntegerProperty interval) {
        super(interval);
    }

    protected String getYChartLabel() {
        return "Avg Latency (\u00B5s)";
    }

    @Override
    protected Number getValue(final LatencyStatPoint point) {
        return point.getLatencyStat().getLat().getAverage();
    }
}
