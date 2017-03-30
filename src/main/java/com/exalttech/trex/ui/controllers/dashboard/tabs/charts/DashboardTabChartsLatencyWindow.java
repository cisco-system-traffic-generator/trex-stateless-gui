package com.exalttech.trex.ui.controllers.dashboard.tabs.charts;

import javafx.beans.property.IntegerProperty;

import java.util.Map;

import com.exalttech.trex.ui.views.statistics.LatencyStatsLoader;
import com.exalttech.trex.util.ArrayHistory;


public class DashboardTabChartsLatencyWindow extends DashboardTabChartsLatencyLine {
    public DashboardTabChartsLatencyWindow(IntegerProperty interval) {
        super(interval);
    }

    protected String getYChartLabel() {
        return "Latency Window (\u00B5s)";
    }

    protected Map<String, ArrayHistory<Number>> getHistory() {
        return LatencyStatsLoader.getInstance().getLatencyWindowHistoryMap();
    }
}
