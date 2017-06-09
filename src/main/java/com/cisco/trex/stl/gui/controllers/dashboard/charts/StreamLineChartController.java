package com.cisco.trex.stl.gui.controllers.dashboard.charts;

import javafx.beans.property.IntegerProperty;
import javafx.scene.chart.XYChart;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.cisco.trex.stl.gui.models.FlowStatPoint;
import com.cisco.trex.stl.gui.storages.PGIDStatsStorage;
import com.cisco.trex.stl.gui.storages.StatsStorage;

import com.exalttech.trex.util.ArrayHistory;
import com.exalttech.trex.util.Formatter;


public abstract class StreamLineChartController extends LineFlowChartController {
    public StreamLineChartController(final IntegerProperty interval) {
        super(interval);
    }

    @Override
    protected void render() {
        getChart().getData().clear();

        final StatsStorage statsStorage = StatsStorage.getInstance();

        final Map<Integer, String> selectedPGIDs = statsStorage.getPGIDsStorage().getSelectedPGIds();

        final PGIDStatsStorage pgIDStatsStorage = statsStorage.getPGIDStatsStorage();
        final Map<Integer, ArrayHistory<FlowStatPoint>> latencyStatPointHistoryMap =
                pgIDStatsStorage.getFlowStatPointHistoryMap();
        final List<XYChart.Series<Double, Number>> seriesList = new LinkedList<>();
        final Formatter formatter = new Formatter();

        synchronized (pgIDStatsStorage.getDataLock()) {
            latencyStatPointHistoryMap.forEach((final Integer pgID, final ArrayHistory<FlowStatPoint> history) -> {
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
                    final FlowStatPoint point = history.get(i);
                    final double time = point.getTime();
                    final Number value = getValue(point);
                    formatter.addValue(value);
                    series.getData().add(new XYChart.Data<>(time - lastTime, value));
                }
                setSeriesColor(series, color);
                seriesList.add(series);
            });
        }

        seriesList.forEach((final XYChart.Series<Double, Number> series) -> {
            series.getData().forEach((final XYChart.Data<Double, Number> data) -> {
                data.setYValue(formatter.getFormattedValue(data.getYValue()));
            });
        });
        getChart().getData().addAll(seriesList);

        getYAxis().setLabel(String.format("%s (%s%s)", getYChartName(), formatter.getUnitsPrefix(), getYChartUnits()));
    }

    protected abstract String getYChartName();

    protected abstract String getYChartUnits();

    protected abstract Number getValue(final FlowStatPoint point);
}
