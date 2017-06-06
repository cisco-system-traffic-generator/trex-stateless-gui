package com.exalttech.trex.ui.controllers.dashboard.tabs.charts;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.AnchorPane;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.cisco.trex.stateless.gui.storages.PGIDStatsStorage;

import com.exalttech.trex.ui.models.stats.LatencyStatPoint;
import com.exalttech.trex.ui.views.storages.StatsStorage;
import com.exalttech.trex.util.ArrayHistory;


public class LatencyHistogram extends FlowChart {
    private static final int HISTOGRAM_SIZE = 11;

    @FXML
    private AnchorPane root;
    @FXML
    private BarChart<String, Long> histogram;
    @FXML
    private CategoryAxis xAxis;

    public void render() {
        histogram.getData().clear();
        xAxis.setAutoRanging(true);

        final StatsStorage statsStorage = StatsStorage.getInstance();

        final Map<Integer, String> selectedPGIDs = statsStorage.getPGIDsStorage().getSelectedPGIds();

        final PGIDStatsStorage pgIDStatsStorage = statsStorage.getPGIDStatsStorage();
        final Map<Integer, ArrayHistory<LatencyStatPoint>> latencyStatPointHistoryMap =
                pgIDStatsStorage.getLatencyStatPointHistoryMap();
        final Map<Integer, LatencyStatPoint> latencyStatPointShadowMap =
                pgIDStatsStorage.getLatencyStatPointShadowMap();
        final String[] histogramKeys = pgIDStatsStorage.getHistogramKeys(HISTOGRAM_SIZE);

        final List<XYChart.Series<String, Long>> seriesList = new LinkedList<>();

        synchronized (pgIDStatsStorage.getLatencyLock()) {
            latencyStatPointHistoryMap.forEach((final Integer pgID, final ArrayHistory<LatencyStatPoint> history) -> {
                if (history == null || history.isEmpty()) {
                    return;
                }

                final String color = selectedPGIDs.get(pgID);
                if (color == null) {
                    return;
                }

                final LatencyStatPoint latencyShadow = latencyStatPointShadowMap.get(pgID);
                final Map<String, Long> shadowHistogram = latencyShadow != null ?
                        latencyShadow.getLatencyStat().getLat().getHistogram() :
                        new HashMap<>();

                final Map<String, Long> histogram = history.last().getLatencyStat().getLat().getHistogram();
                final XYChart.Series<String, Long> series = new XYChart.Series<>();
                series.setName(String.valueOf(pgID));
                for (final String key : histogramKeys) {
                    final long value = histogram.getOrDefault(key, 0L);
                    final long shadowValue = shadowHistogram.getOrDefault(key, 0L);
                    series.getData().add(new XYChart.Data<>(key, value - shadowValue));
                }
                setSeriesColor(series, color);
                seriesList.add(series);
            });
        }

        if (seriesList.isEmpty()) {
            return;
        }

        histogram.getData().addAll(seriesList);
        xAxis.setAutoRanging(true);
    }

    @Override
    protected String getResourceName() {
        return "/fxml/Dashboard/tabs/charts/LatencyHistogram.fxml";
    }

    @Override
    protected Node getRoot() {
        return root;
    }

    private void setSeriesColor(final XYChart.Series<String, Long> series, final String color) {
        for (final XYChart.Data<String, Long> data : series.getData()) {
            data.nodeProperty().addListener((ObservableValue<? extends Node> observable, Node oldValue, Node newValue) -> {
                if (oldValue == null && newValue != null) {
                    newValue.setStyle(String.format("-fx-bar-fill: %s;", color));
                }
            });
        }
    }
}
