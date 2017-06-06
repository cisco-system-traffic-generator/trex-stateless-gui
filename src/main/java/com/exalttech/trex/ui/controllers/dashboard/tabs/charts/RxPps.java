package com.exalttech.trex.ui.controllers.dashboard.tabs.charts;

import javafx.beans.property.IntegerProperty;

import com.cisco.trex.stl.gui.models.FlowStatPoint;


public class RxPps extends StreamLineChart {
    public RxPps(final IntegerProperty interval) {
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
