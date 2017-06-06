package com.exalttech.trex.ui.controllers.dashboard.tabs.charts;

import javafx.beans.property.IntegerProperty;
import javafx.scene.chart.XYChart;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.cisco.trex.stl.gui.storages.PGIDStatsStorage;
import com.cisco.trex.stl.gui.storages.StatsStorage;

import com.cisco.trex.stl.gui.models.LatencyStatPoint;
import com.exalttech.trex.util.ArrayHistory;


public abstract class LatencyLineChart extends LineFlowChart {
    public LatencyLineChart(final IntegerProperty interval) {
        super(interval);

        getYAxis().setLabel(getYChartLabel());
    }

    protected abstract String getYChartLabel();

    @Override
    protected void render() {
        getChart().getData().clear();

        final StatsStorage statsStorage = StatsStorage.getInstance();

        final Map<Integer, String> selectedPGIDs = statsStorage.getPGIDsStorage().getSelectedPGIds();

        final PGIDStatsStorage pgIDStatsStorage = statsStorage.getPGIDStatsStorage();
        final Map<Integer, ArrayHistory<LatencyStatPoint>> latencyStatPointHistoryMap =
                pgIDStatsStorage.getLatencyStatPointHistoryMap();
        final List<XYChart.Series<Double, Number>> seriesList = new LinkedList<>();

        synchronized (pgIDStatsStorage.getLatencyLock()) {
            latencyStatPointHistoryMap.forEach((final Integer pgID, final ArrayHistory<LatencyStatPoint> history) -> {
                if (history == null || history.isEmpty()) {
                    return;
                }

                final String color = selectedPGIDs.get(pgID);
                if (color == null) {
                    return;
                }

                final double lastTime = history.last().getTime();

                final XYChart.Series<Double, Number> series = new XYChart.Series<>();
                series.setName(String.valueOf(pgID));
                int size = history.size();
                for (int i = 0; i < size; ++i) {
                    final LatencyStatPoint point = history.get(i);
                    final double time = point.getTime();
                    series.getData().add(new XYChart.Data<>(time - lastTime, getValue(point)));
                }
                setSeriesColor(series, color);
                seriesList.add(series);
            });
        }

        getChart().getData().addAll(seriesList);
    }

    protected abstract Number getValue(final LatencyStatPoint point);
}
