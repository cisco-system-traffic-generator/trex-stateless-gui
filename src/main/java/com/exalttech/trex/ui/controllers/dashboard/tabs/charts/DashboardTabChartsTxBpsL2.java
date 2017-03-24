package com.exalttech.trex.ui.controllers.dashboard.tabs.charts;

import javafx.beans.property.IntegerProperty;

import java.util.Set;

import com.exalttech.trex.ui.models.stats.flow.StatsFlowStream;


public class DashboardTabChartsTxBpsL2 extends DashboardTabChartsFlow {
    public DashboardTabChartsTxBpsL2(IntegerProperty interval) {
        super(interval);
    }

    protected String getYChartName() {
        return "Tx Bps L2";
    }

    protected String getYChartUnits() {
        return "B/s";
    }

    protected Number calcValue(Set<Integer> visiblePorts, StatsFlowStream point) {
        return point.calcTotalTxBpsL2(visiblePorts);
    }
}
