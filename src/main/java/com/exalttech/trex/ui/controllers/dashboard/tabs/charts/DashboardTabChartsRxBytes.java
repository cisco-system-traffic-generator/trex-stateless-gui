package com.exalttech.trex.ui.controllers.dashboard.tabs.charts;

import javafx.beans.property.IntegerProperty;

import java.util.Set;

import com.exalttech.trex.ui.models.stats.flow.StatsFlowStream;


public class DashboardTabChartsRxBytes extends DashboardTabChartsFlow {
    public DashboardTabChartsRxBytes(IntegerProperty interval) {
        super(interval);
    }

    protected String getName() {
        return "Rx (B)";
    }

    protected Number calcValue(Set<Integer> visiblePorts, StatsFlowStream point) {
        return point.calcTotalRxBytes(visiblePorts);
    }
}
