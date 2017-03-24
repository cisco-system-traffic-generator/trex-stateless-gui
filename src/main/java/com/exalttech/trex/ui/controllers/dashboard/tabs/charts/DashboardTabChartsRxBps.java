package com.exalttech.trex.ui.controllers.dashboard.tabs.charts;

import javafx.beans.property.IntegerProperty;

import java.util.Set;

import com.exalttech.trex.ui.models.stats.flow.StatsFlowStream;


public class DashboardTabChartsRxBps extends DashboardTabChartsFlow {
    public DashboardTabChartsRxBps(IntegerProperty interval) {
        super(interval);
    }

    protected String getYChartName() {
        return "Rx Bps L2";
    }

    protected String getYChartUnits() {
        return "B/s";
    }

    protected Number calcValue(Set<Integer> visiblePorts, StatsFlowStream point) {
        return point.calcTotalRxBps(visiblePorts);
    }
}
