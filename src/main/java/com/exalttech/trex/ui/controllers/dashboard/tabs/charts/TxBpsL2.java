package com.exalttech.trex.ui.controllers.dashboard.tabs.charts;

import javafx.beans.property.IntegerProperty;

import com.cisco.trex.stl.gui.models.FlowStatPoint;


public class TxBpsL2 extends StreamLineChart {
    public TxBpsL2(final IntegerProperty interval) {
        super(interval);
    }

    protected String getYChartName() {
        return "Tx bps L2";
    }

    protected String getYChartUnits() {
        return "b/s";
    }

    protected Number getValue(final FlowStatPoint point) {
        return point.getRbsL2();
    }
}
