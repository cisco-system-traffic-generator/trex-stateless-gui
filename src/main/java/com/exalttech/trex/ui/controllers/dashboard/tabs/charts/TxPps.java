package com.exalttech.trex.ui.controllers.dashboard.tabs.charts;

import javafx.beans.property.IntegerProperty;

import com.cisco.trex.stl.gui.models.FlowStatPoint;


public class TxPps extends StreamLineChart {
    public TxPps(final IntegerProperty interval) {
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
