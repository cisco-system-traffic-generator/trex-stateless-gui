package com.exalttech.trex.ui.controllers.dashboard.tabs.charts;

import javafx.beans.property.IntegerProperty;

import java.util.Set;

import com.exalttech.trex.ui.models.stats.flow.StatsFlowStream;


public class DashboardTabChartsTxBytes extends DashboardTabChartsFlow {
    public DashboardTabChartsTxBytes(IntegerProperty interval) {
        super(interval);
    }

    protected String getYChartName() {
        return "Tx bytes";
    }

    protected String getYChartUnits() {
        return "B";
    }

    protected Number calcValue(Set<Integer> visiblePorts, StatsFlowStream point) {
        return point.calcTotalTxBytes(visiblePorts);
    }
}
