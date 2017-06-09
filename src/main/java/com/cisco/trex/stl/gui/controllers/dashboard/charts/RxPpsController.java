package com.cisco.trex.stl.gui.controllers.dashboard.charts;

import javafx.beans.property.IntegerProperty;

import com.cisco.trex.stl.gui.models.FlowStatPoint;


public class RxPpsController extends StreamLineChartController {
    public RxPpsController(final IntegerProperty interval) {
        super(interval);
    }

    protected String getYChartName() {
        return "Rx pps";
    }

    protected String getYChartUnits() {
        return "pkt/s";
    }

    protected Number getValue(final FlowStatPoint point) {
        return point.getRps();
    }
}
