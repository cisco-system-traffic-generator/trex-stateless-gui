package com.exalttech.trex.ui.controllers.dashboard.tabs.charts;

import javafx.beans.property.IntegerProperty;

import java.util.Set;

import com.exalttech.trex.ui.models.stats.flow.StatsFlowStream;


public class DashboardTabChartsRxBytes extends DashboardTabChartsFlow {
    public DashboardTabChartsRxBytes(IntegerProperty interval) {
        super(interval);
    }

    protected String getYChartName() {
        return "Rx bytes";
    }

    protected String getYChartUnits() {
        return "B";
    }

    protected Number calcValue(Set<Integer> visiblePorts, StatsFlowStream point) {
        return point.calcTotalRxBytes(visiblePorts);
    }
}
