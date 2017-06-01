package com.exalttech.trex.ui.controllers.dashboard.tabs.charts;

import javafx.beans.property.IntegerProperty;

import com.exalttech.trex.ui.models.stats.flow.StatsFlowStream;


public class DashboardTabChartsRxPps extends DashboardTabChartsFlow {
    public DashboardTabChartsRxPps(IntegerProperty interval) {
        super(interval);
    }

    protected String getYChartName() {
        return "Rx pps";
    }

    protected String getYChartUnits() {
        return "pkt/s";
    }

    protected Number calcValue(StatsFlowStream point) {
        return point.calcTotalRxPps();
    }
}
