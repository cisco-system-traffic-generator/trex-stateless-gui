package com.cisco.trex.stl.gui.controllers.dashboard.charts;

import com.exalttech.trex.application.TrexApp;
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

import com.cisco.trex.stl.gui.models.LatencyStatPoint;
import com.cisco.trex.stl.gui.storages.PGIDStatsStorage;
import com.cisco.trex.stl.gui.storages.StatsStorage;

import com.exalttech.trex.util.ArrayHistory;


public class LatencyHistogramController extends FlowChartController {
    private static final int HISTOGRAM_SIZE = 11;

    @FXML
    private AnchorPane root;
    @FXML
    private BarChart<String, Long> histogram;
    @FXML
    private CategoryAxis xAxis;

    StatsStorage statsStorage = TrexApp.injector.getInstance(StatsStorage.class);

    public void render() {
        histogram.getData().clear();
        xAxis.setAutoRanging(true);

        final Map<Integer, String> selectedPGIDs = statsStorage.getPGIDsStorage().getSelectedPGIds();

        final PGIDStatsStorage pgIDStatsStorage = statsStorage.getPGIDStatsStorage();
        final Map<Integer, ArrayHistory<LatencyStatPoint>> latencyStatPointHistoryMap =
                pgIDStatsStorage.getLatencyStatPointHistoryMap();
        final String[] histogramKeys = pgIDStatsStorage.getHistogramKeys(HISTOGRAM_SIZE);

        final List<XYChart.Series<String, Long>> seriesList = new LinkedList<>();

        synchronized (pgIDStatsStorage.getDataLock()) {
            latencyStatPointHistoryMap.forEach((final Integer pgID, final ArrayHistory<LatencyStatPoint> history) -> {
                if (history == null || history.isEmpty()) {
                    return;
                }

                final String color = selectedPGIDs.get(pgID);
                if (color == null) {
                    return;
                }

                final Map<String, Long> histogram = history.last().getLatencyStat().getLat().getHistogram();
                final XYChart.Series<String, Long> series = new XYChart.Series<>();
                series.setName(String.valueOf(pgID));
                for (final String key : histogramKeys) {
                    final long value = histogram.getOrDefault(key, 0L);
                    series.getData().add(new XYChart.Data<>(key, value));
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
        return "/fxml/dashboard/charts/LatencyHistogram.fxml";
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
