package com.exalttech.trex.ui.controllers.dashboard.tabs.charts;

import javafx.beans.property.IntegerProperty;

import java.util.Set;

import com.exalttech.trex.ui.models.stats.flow.StatsFlowStream;


public class DashboardTabChartsTxPps extends DashboardTabChartsFlow {
    public DashboardTabChartsTxPps(IntegerProperty interval) {
        super(interval);
    }

    protected String getYChartLabel() {
        return "Tx pps (pkt/s)";
    }

    protected Number calcValue(Set<Integer> visiblePorts, StatsFlowStream point) {
        return point.calcTotalTxPps(visiblePorts);
    }
}
