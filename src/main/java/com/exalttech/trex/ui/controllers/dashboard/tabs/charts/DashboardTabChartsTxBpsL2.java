package com.exalttech.trex.ui.controllers.dashboard.tabs.charts;

import javafx.beans.property.IntegerProperty;

import com.exalttech.trex.ui.models.stats.flow.StatsFlowStream;


public class DashboardTabChartsTxBpsL2 extends DashboardTabChartsFlow {
    public DashboardTabChartsTxBpsL2(IntegerProperty interval) {
        super(interval);
    }

    protected String getYChartName() {
        return "Tx bps L2";
    }

    protected String getYChartUnits() {
        return "b/s";
    }

    protected Number calcValue(StatsFlowStream point) {
        return point.calcTotalTxBpsL2();
    }
}
