package com.exalttech.trex.ui.controllers.dashboard.tabs.charts;

import javafx.beans.property.IntegerProperty;

import com.cisco.trex.stateless.gui.models.LatencyStatPoint;


public class LatencyJitter extends LatencyLineChart {
    public LatencyJitter(final IntegerProperty interval) {
        super(interval);
    }

    @Override
    protected String getYChartLabel() {
        return "Latency Jitter (\u00B5s)";
    }

    @Override
    protected Number getValue(final LatencyStatPoint point) {
        return point.getLatencyStat().getLat().getJit();
    }
}
