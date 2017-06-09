package com.cisco.trex.stl.gui.controllers.dashboard.charts;

import javafx.beans.property.IntegerProperty;

import com.cisco.trex.stl.gui.models.LatencyStatPoint;


public class MaxLatencyController extends LatencyLineChartController {
    public MaxLatencyController(final IntegerProperty interval) {
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
