package com.exalttech.trex.ui.controllers.dashboard.tabs.charts;

import javafx.beans.property.IntegerProperty;

import java.util.Set;

import com.exalttech.trex.ui.models.stats.flow.StatsFlowStream;


public class DashboardTabChartsTxBpsL1 extends DashboardTabChartsFlow {
    public DashboardTabChartsTxBpsL1(IntegerProperty interval) {
        super(interval);
    }

    protected String getYChartName() {
        return "Tx bps L1";
    }

    protected String getYChartUnits() {
        return "b/s";
    }

    protected Number calcValue(Set<Integer> visiblePorts, StatsFlowStream point) {
        return point.calcTotalTxBpsL1(visiblePorts);
    }
}
