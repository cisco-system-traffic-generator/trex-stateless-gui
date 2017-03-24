package com.exalttech.trex.ui.controllers.dashboard.tabs.charts;

import javafx.beans.property.IntegerProperty;

import java.util.Set;

import com.exalttech.trex.ui.models.stats.flow.StatsFlowStream;


public class DashboardTabChartsRxPkts extends DashboardTabChartsFlow {
    public DashboardTabChartsRxPkts(IntegerProperty interval) {
        super(interval);
    }

    protected String getYChartName() {
        return "Rx pkts";
    }

    protected String getYChartUnits() {
        return "pkt";
    }

    protected Number calcValue(Set<Integer> visiblePorts, StatsFlowStream point) {
        return point.calcTotalRxPkts(visiblePorts);
    }
}
