package com.cisco.trex.stl.gui.controllers.dashboard.charts;

import javafx.beans.property.IntegerProperty;

import com.cisco.trex.stl.gui.models.LatencyStatPoint;


public class LatencyJitterController extends LatencyLineChartController {
    public LatencyJitterController(final IntegerProperty interval) {
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
