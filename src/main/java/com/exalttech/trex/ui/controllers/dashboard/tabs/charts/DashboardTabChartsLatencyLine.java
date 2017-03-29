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


public abstract class DashboardTabChartsLatencyLine extends DashboardTabChartsLine {
    public DashboardTabChartsLatencyLine(IntegerProperty interval) {
        super(interval);
    }

    public void update(Set<Integer> visiblePorts, Set<String> visibleStreams, int streamsCount) {
        getChart().getData().clear();

        if (visibleStreams != null && visibleStreams.isEmpty()) {
            return;
        }

        Map<String, ArrayHistory<Number>> streams = getHistory();
        List<XYChart.Series<Number, Number>> seriesList = new LinkedList<>();
        AtomicInteger streamIndex = new AtomicInteger(0);
        streams.forEach((String stream, ArrayHistory<Number> history) -> {
            if (streamIndex.get() >= streamsCount || (visibleStreams != null && !visibleStreams.contains(stream))) {
                return;
            }

            XYChart.Series series = new XYChart.Series();
            series.setName(stream);
            int size = history.size();
            for (int i = 0; i < size; ++i) {
                series.getData().add(new XYChart.Data<>(i + 1 - size, history.get(i)));
            }

            seriesList.add(series);

            streamIndex.getAndAdd(1);
        });
        getChart().getData().clear();
        getChart().getData().addAll(seriesList);
    }

    protected String getXChartLabel() {
        return "";
    }

    protected abstract Map<String, ArrayHistory<Number>> getHistory();
}
