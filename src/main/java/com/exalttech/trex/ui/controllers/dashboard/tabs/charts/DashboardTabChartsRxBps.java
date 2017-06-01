package com.exalttech.trex.ui.controllers.dashboard.tabs.charts;

import javafx.beans.property.IntegerProperty;

import com.exalttech.trex.ui.models.stats.flow.StatsFlowStream;


public class DashboardTabChartsRxBps extends DashboardTabChartsFlow {
    public DashboardTabChartsRxBps(IntegerProperty interval) {
        super(interval);
    }

    protected String getYChartName() {
        return "Rx bps L2";
    }

    protected String getYChartUnits() {
        return "b/s";
    }

    protected Number calcValue(StatsFlowStream point) {
        return point.calcTotalRxBps();
    }
}
