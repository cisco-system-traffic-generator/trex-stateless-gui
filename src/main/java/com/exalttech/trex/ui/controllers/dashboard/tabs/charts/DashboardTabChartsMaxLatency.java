package com.exalttech.trex.ui.controllers.dashboard.tabs.charts;

import com.exalttech.trex.ui.views.statistics.StatsLoader;
import com.exalttech.trex.util.ArrayHistory;
import javafx.beans.property.IntegerProperty;
import javafx.scene.chart.XYChart;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;


public class DashboardTabChartsMaxLatency extends DashboardTabChartsLatencyLine {
    public DashboardTabChartsMaxLatency(IntegerProperty interval) {
        super(interval);
    }

    protected String getYChartLabel() {
        return "Max Latency (\u00B5s)";
    }

    protected Map<String, ArrayHistory<Number>> getHistory() {
        return StatsLoader.getInstance().getMaxLatencyHistory();
    }
}
