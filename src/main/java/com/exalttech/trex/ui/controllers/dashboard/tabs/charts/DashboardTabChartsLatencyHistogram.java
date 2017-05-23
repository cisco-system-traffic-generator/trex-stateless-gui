package com.exalttech.trex.ui.controllers.dashboard.tabs.charts;

import com.exalttech.trex.ui.views.statistics.LatencyStatsLoader;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.AnchorPane;

import java.util.*;

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

    public void update(final Map<Integer, String> selectedPGIds) {
        histogram.getData().clear();
        xAxis.setAutoRanging(true);

        final Map<String, Map<String, Long>> histogramMap = LatencyStatsLoader.getInstance().getHistogramMap();
        final String[] histogramKeys = LatencyStatsLoader.getInstance().getHistogramKeys(HISTOGRAM_SIZE);
        final List<XYChart.Series<String, Number>> seriesList = new LinkedList<>();
        synchronized (histogramMap) {
            synchronized (histogramKeys) {
                for (final Map.Entry<Integer, String> entry : selectedPGIds.entrySet()) {
                    final String stream = String.valueOf(entry.getKey());
                    final Map<String, Long> histogram = histogramMap.get(stream);
                    final XYChart.Series<String, Number> series = new XYChart.Series<>();
                    if (histogram != null) {
                        series.setName(stream);
                        for (final String key : histogramKeys) {
                            series.getData().add(new XYChart.Data<>(key, histogram.getOrDefault(key, 0L)));
                        }
                    }
                    setSeriesColor(series, entry.getValue());
                    seriesList.add(series);
                }
            }
        }

        if (seriesList.isEmpty()) {
            return;
        }

        histogram.getData().addAll(seriesList);
        xAxis.setAutoRanging(true);
    }

    protected void setSeriesColor(final XYChart.Series<String, Number> series, final String color) {
        series.nodeProperty().addListener((ObservableValue<? extends Node> observable, Node oldValue, Node newValue) -> {
            if (oldValue == null && newValue != null) {
                series.getNode().setStyle(String.format("-fx-bar-fill: %s;", color));
            }
        });
    }
}
