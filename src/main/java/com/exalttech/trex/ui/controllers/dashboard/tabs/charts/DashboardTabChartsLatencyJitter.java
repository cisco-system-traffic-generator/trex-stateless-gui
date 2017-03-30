package com.exalttech.trex.ui.controllers.dashboard.tabs.charts;

import javafx.beans.property.IntegerProperty;

import com.exalttech.trex.util.ArrayHistory;
import com.exalttech.trex.ui.views.statistics.LatencyStatsLoader;

import java.util.Map;


public class DashboardTabChartsLatencyJitter extends DashboardTabChartsLatencyLine {
    public DashboardTabChartsLatencyJitter(IntegerProperty interval) {
        super(interval);
    }

    protected String getYChartLabel() {
        return "Latency Jitter (\u00B5s)";
    }

    protected Map<String, ArrayHistory<Number>> getHistory() {
        return LatencyStatsLoader.getInstance().getLatencyJitterHistoryMap();
    }
}
