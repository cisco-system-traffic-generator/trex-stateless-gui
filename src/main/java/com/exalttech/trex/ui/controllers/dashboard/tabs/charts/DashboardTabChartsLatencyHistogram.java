package com.exalttech.trex.ui.controllers.dashboard.tabs.charts;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.AnchorPane;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import com.exalttech.trex.ui.models.stats.latency.StatsLatencyStream;
import com.exalttech.trex.ui.models.stats.latency.StatsLatencyStreamLatency;
import com.exalttech.trex.ui.views.statistics.StatsLoader;
import com.exalttech.trex.util.Initialization;


public class DashboardTabChartsLatencyHistogram extends AnchorPane implements DashboardTabChartsUpdatable {
    private static final int HISTOGRAM_SIZE = 11;

    @FXML
    private BarChart<String, Number> histogram;
    @FXML
    private CategoryAxis xAxis;

    public DashboardTabChartsLatencyHistogram() {
        Initialization.initializeFXML(this, "/fxml/Dashboard/tabs/charts/DashboardTabChartsLatencyHistogram.fxml");
    }

    public void update(Set<Integer> visiblePorts, Set<String> visibleStreams, int streamsCount) {
        histogram.getData().clear();
        xAxis.setAutoRanging(true);

        if (visibleStreams != null && visibleStreams.isEmpty()) {
            return;
        }

        Map<String, StatsLatencyStream> latencyStatsByStreams = StatsLoader.getInstance().getLatencyStatsMap();

        final TreeSet<Integer> keys = new TreeSet<>();
        final AtomicInteger statsIndex = new AtomicInteger(0);
        latencyStatsByStreams.forEach((final String stream, final StatsLatencyStream statslatencyStream) -> {
            if (statsIndex.get() >= streamsCount || (visibleStreams != null && !visibleStreams.contains(stream))) {
                return;
            }
            statslatencyStream.getLatency().getHistogram().keySet().forEach((final String key) -> {
                keys.add(Integer.parseInt(key));
            });
        });
        final int histogramSize = Math.min(keys.size(), HISTOGRAM_SIZE);
        final String[] keysOrder = new String[histogramSize];
        for (int i = 0; i < histogramSize; ++i) {
            keysOrder[i] = String.valueOf(keys.pollLast());
        }

        List<XYChart.Series<String, Number>> seriesList = new LinkedList<>();
        Set<String> categories = new HashSet<>();
        AtomicInteger streamIndex = new AtomicInteger(0);
        latencyStatsByStreams.forEach((String stream, StatsLatencyStream latencyStats) -> {
            if (streamIndex.get() >= streamsCount || (visibleStreams != null && !visibleStreams.contains(stream))) {
                return;
            }

            if (latencyStats == null) {
                return;
            }

            StatsLatencyStreamLatency latency = latencyStats.getLatency();
            if (latency == null) {
                return;
            }

            Map<String, Integer> histogram = latency.getHistogram();
            if (histogram == null || histogram.isEmpty()) {
                return;
            }

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(stream);
            for (final String key : keysOrder) {
                Integer value = histogram.get(String.valueOf(key));
                if (value == null) {
                    value = 0;
                }
                categories.add(key);
                series.getData().add(new XYChart.Data<>(key, value));
            }
            seriesList.add(series);

            streamIndex.getAndAdd(1);
        });

        if (seriesList.isEmpty()) {
            return;
        }

        histogram.getData().addAll(seriesList);
        List<String> categoriesList = new ArrayList<>();
        categoriesList.addAll(categories);
        categoriesList.sort(new Comparator<String>() {
            @Override
            public int compare(String category1, String category2) {
                return Integer.parseInt(category1) - Integer.parseInt(category2);
            }
        });
        xAxis.setCategories(FXCollections.observableArrayList(categoriesList));
        xAxis.setAutoRanging(true);
    }
}
