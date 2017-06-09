package com.cisco.trex.stl.gui.controllers.dashboard.charts;

import javafx.beans.property.IntegerProperty;

import com.cisco.trex.stl.gui.models.FlowStatPoint;


public class TxPpsController extends StreamLineChartController {
    public TxPpsController(final IntegerProperty interval) {
        super(interval);
    }

    protected String getYChartName() {
        return "Tx pps";
    }

    protected String getYChartUnits() {
        return "pkt/s";
    }

    protected Number getValue(final FlowStatPoint point) {
        return point.getTps();
    }
}
