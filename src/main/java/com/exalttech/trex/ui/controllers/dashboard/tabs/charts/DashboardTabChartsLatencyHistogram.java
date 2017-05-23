package com.exalttech.trex.ui.controllers.dashboard.tabs.charts;

import com.exalttech.trex.ui.views.statistics.LatencyStatsLoader;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.AnchorPane;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

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

    public void update(int streamsCount) {
        histogram.getData().clear();
        xAxis.setAutoRanging(true);

        final Map<String, Map<String, Long>> histogramMap = LatencyStatsLoader.getInstance().getHistogramMap();
        final String[] histogramKeys = LatencyStatsLoader.getInstance().getHistogramKeys(HISTOGRAM_SIZE);
        final AtomicInteger streamIndex = new AtomicInteger(0);
        final List<XYChart.Series<String, Number>> seriesList = new LinkedList<>();
        synchronized (histogramMap) {
            synchronized (histogramKeys) {
                histogramMap.forEach((final String stream, final Map<String, Long> histogram) -> {
                    if (streamIndex.get() >= streamsCount) {
                        return;
                    }

                    final XYChart.Series<String, Number> series = new XYChart.Series<>();
                    series.setName(stream);
                    for (final String key : histogramKeys) {
                        series.getData().add(new XYChart.Data<>(key, histogram.getOrDefault(key, 0L)));
                    }
                    seriesList.add(series);

                    streamIndex.getAndAdd(1);
                });
            }
        }

        if (seriesList.isEmpty()) {
            return;
        }

        histogram.getData().addAll(seriesList);
        xAxis.setAutoRanging(true);
    }
}
