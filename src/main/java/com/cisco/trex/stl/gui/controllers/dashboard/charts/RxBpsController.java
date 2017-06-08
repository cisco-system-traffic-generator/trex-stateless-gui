package com.cisco.trex.stl.gui.controllers.dashboard.charts;

import javafx.beans.property.IntegerProperty;

import com.cisco.trex.stl.gui.models.FlowStatPoint;


public class RxBpsController extends StreamLineChartController {
    public RxBpsController(final IntegerProperty interval) {
        super(interval);
    }

    protected String getYChartName() {
        return "Rx bps L2";
    }

    protected String getYChartUnits() {
        return "b/s";
    }

    protected Number getValue(final FlowStatPoint point) {
        return point.getRbsL2();
    }
}
