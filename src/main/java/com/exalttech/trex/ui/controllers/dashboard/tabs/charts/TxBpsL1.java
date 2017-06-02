package com.exalttech.trex.ui.controllers.dashboard.tabs.charts;

import javafx.beans.property.IntegerProperty;

import com.exalttech.trex.ui.models.stats.FlowStatPoint;


public class TxBpsL1 extends StreamLineChart {
    public TxBpsL1(final IntegerProperty interval) {
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
