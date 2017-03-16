package com.exalttech.trex.ui.controllers.dashboard.tabs.charts;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.AnchorPane;

import java.util.*;

import com.exalttech.trex.ui.models.stats.latency.StatsLatencyStream;
import com.exalttech.trex.ui.models.stats.latency.StatsLatencyStreamLatency;
import com.exalttech.trex.ui.views.statistics.StatsLoader;
import com.exalttech.trex.util.Initialization;


public class DashboardTabChartsLatencyHistogram extends AnchorPane implements DashboardTabChartsUpdatable {
    @FXML
    private BarChart<String, Number> histogram;
    @FXML
    private CategoryAxis xAxis;

    public DashboardTabChartsLatencyHistogram() {
        Initialization.initializeFXML(this, "/fxml/Dashboard/tabs/charts/DashboardTabChartsLatencyHistogram.fxml");
    }

    public void update(Set<Integer> visiblePorts, Set<String> visibleStreams) {
        histogram.getData().clear();
        xAxis.setAutoRanging(true);

        if (visibleStreams != null && visibleStreams.isEmpty()) {
            return;
        }

        Map<String, StatsLatencyStream> latencyStatsByStreams = StatsLoader.getInstance().getLatencyStatsMap();

        List<XYChart.Series<String, Number>> seriesList = new LinkedList<>();
        Set<String> categories = new HashSet<>();
        latencyStatsByStreams.forEach((String stream, StatsLatencyStream latencyStats) -> {
            if (visibleStreams != null && !visibleStreams.contains(stream)) {
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
            histogram.forEach((String key, Integer value) -> {
                categories.add(key);
                series.getData().add(new XYChart.Data<>(key, value));
            });
            seriesList.add(series);
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
