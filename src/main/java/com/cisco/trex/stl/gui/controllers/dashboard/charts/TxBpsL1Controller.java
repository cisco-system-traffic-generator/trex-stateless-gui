package com.cisco.trex.stl.gui.controllers.dashboard.charts;

import javafx.beans.property.IntegerProperty;

import com.cisco.trex.stl.gui.models.FlowStatPoint;


public class TxBpsL1Controller extends StreamLineChartController {
    public TxBpsL1Controller(final IntegerProperty interval) {
        super(interval);
    }

    protected String getYChartName() {
        return "Tx bps L1";
    }

    protected String getYChartUnits() {
        return "b/s";
    }

    protected Number getValue(final FlowStatPoint point) {
        return point.getTbsL1();
    }
}
