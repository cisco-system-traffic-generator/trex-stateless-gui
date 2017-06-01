package com.exalttech.trex.ui.controllers.dashboard.tabs.charts;

import javafx.beans.property.IntegerProperty;
import javafx.scene.chart.XYChart;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.exalttech.trex.util.ArrayHistory;


public abstract class DashboardTabChartsLatencyLine extends DashboardTabChartsLine {
    public DashboardTabChartsLatencyLine(IntegerProperty interval) {
        super(interval);
    }

    public void update(final Map<Integer, String> selectedPGIds) {
        getChart().getData().clear();

        Map<String, ArrayHistory<Number>> streams = getHistory();
        List<XYChart.Series<Number, Number>> seriesList = new LinkedList<>();
        synchronized (streams) {
            for (final Map.Entry<Integer, String> entry : selectedPGIds.entrySet()) {
                final String stream = String.valueOf(entry.getKey());
                final ArrayHistory<Number> history = streams.get(stream);
                final XYChart.Series series = new XYChart.Series();
                series.setName(stream);
                if (history != null) {
                    int size = history.size();
                    for (int i = 0; i < size; ++i) {
                        series.getData().add(new XYChart.Data<>(i + 1 - size, history.get(i)));
                    }
                }
                setSeriesColor(series, entry.getValue());
                seriesList.add(series);
            }
        }
        getChart().getData().clear();
        getChart().getData().addAll(seriesList);
    }

    protected String getXChartLabel() {
        return "";
    }

    protected abstract Map<String, ArrayHistory<Number>> getHistory();
}
