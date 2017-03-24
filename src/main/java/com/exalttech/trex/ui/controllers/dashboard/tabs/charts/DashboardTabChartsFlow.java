package com.exalttech.trex.ui.controllers.dashboard.tabs.charts;

import javafx.beans.property.IntegerProperty;
import javafx.scene.chart.XYChart;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import com.exalttech.trex.ui.models.stats.flow.StatsFlowStream;
import com.exalttech.trex.ui.views.statistics.StatsLoader;
import com.exalttech.trex.util.ArrayHistory;
import com.exalttech.trex.util.Formatter;


public abstract class DashboardTabChartsFlow extends DashboardTabChartsLine {
    public DashboardTabChartsFlow(IntegerProperty interval) {
        super(interval);
    }

    public void update(Set<Integer> visiblePorts, Set<String> visibleStreams, int streamsCount) {
        getChart().getData().clear();

        if (visibleStreams != null && visibleStreams.isEmpty()) {
            return;
        }

        StatsLoader statsLoader = StatsLoader.getInstance();
        Map<String, ArrayHistory<StatsFlowStream>> streams = statsLoader.getFlowStatsHistoryMap();
        double time = statsLoader.getFlowStatsLastTime();
        List<XYChart.Series<Number, Number>> seriesList = new LinkedList<>();
        AtomicInteger streamIndex = new AtomicInteger(0);
        final Formatter formatter = new Formatter();
        streams.forEach((String stream, ArrayHistory<StatsFlowStream> history) -> {
            if (streamIndex.get() >= streamsCount || (visibleStreams != null && !visibleStreams.contains(stream))) {
                return;
            }

            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(stream);
            history.forEach((StatsFlowStream point) -> {
                Number value = calcValue(visiblePorts, point);
                formatter.addValue(value);
                series.getData().add(new XYChart.Data<>(point.getTime() - time, value));
            });

            seriesList.add(series);

            streamIndex.getAndAdd(1);
        });

        seriesList.forEach((XYChart.Series<Number, Number> series) -> {
            series.getData().forEach((XYChart.Data<Number, Number> data) -> {
                data.setYValue(formatter.getFormattedValue(data.getYValue()));
            });
        });

        getChart().getData().addAll(seriesList);

        getYAxis().setLabel(String.format("%s (%s%s)", getYChartName(), formatter.getUnitsPrefix(), getYChartUnits()));
    }

    protected abstract String getYChartName();

    protected abstract String getYChartUnits();

    protected abstract Number calcValue(Set<Integer> visiblePorts, StatsFlowStream point);

    protected String getXChartLabel() {
        return "Time (s)";
    }

    protected String getYChartLabel() {
        return String.format("%s (%s)", getYChartName(), getYChartUnits());
    }
}
