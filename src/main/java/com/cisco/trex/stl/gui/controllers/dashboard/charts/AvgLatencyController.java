package com.cisco.trex.stl.gui.controllers.dashboard.charts;

import javafx.beans.property.IntegerProperty;

import com.cisco.trex.stl.gui.models.LatencyStatPoint;


public class AvgLatencyController extends LatencyLineChartController {
    public AvgLatencyController(final IntegerProperty interval) {
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
