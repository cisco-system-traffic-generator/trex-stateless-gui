package com.exalttech.trex.ui.controllers.dashboard.tabs.latency;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import com.exalttech.trex.ui.models.stats.flow.StatsFlowStream;
import com.exalttech.trex.ui.models.stats.latency.LatencyInfo;
import com.exalttech.trex.ui.views.statistics.cells.CellType;
import com.exalttech.trex.ui.views.statistics.cells.HeaderCell;
import com.exalttech.trex.ui.views.statistics.cells.StatisticLabelCell;
import com.exalttech.trex.ui.views.statistics.LatencyStatsLoader;
import com.exalttech.trex.ui.views.statistics.StatsLoader;
import com.exalttech.trex.util.ArrayHistory;
import com.exalttech.trex.util.Initialization;
import com.exalttech.trex.util.Util;


public class DashboardTabLatency extends AnchorPane {
    private static final int FIRST_COLUMN_WIDTH = 120;
    private static final int COLUMN_WIDTH = 150;
    private static final int WINDOW_SIZE = 10;
    private static final int HISTOGRAM_SIZE = 11;

    @FXML
    private ToggleGroup toggleGroupMode;
    @FXML
    private GridPane table;

    public DashboardTabLatency() {
        Initialization.initializeFXML(this, "/fxml/Dashboard/tabs/latency/DashboardTabLatency.fxml");

        toggleGroupMode.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if (newValue == null) {
                    oldValue.setSelected(true);
                } else {
                    update();
                }
            }
        });
    }

    public void update() {
        if (((ToggleButton)toggleGroupMode.getSelectedToggle()).getText().equals("Window")) {
            renderWindow();
        } else {
            renderHistogram();
        }
    }

    private void renderWindow() {
        table.getChildren().clear();

        int hCol = 0;
        table.add(new HeaderCell(FIRST_COLUMN_WIDTH, "PG ID"), 0, hCol++);
        table.add(new StatisticLabelCell("Tx pkt", FIRST_COLUMN_WIDTH, hCol%2 == 0, CellType.DEFAULT_CELL, false), 0, hCol++);
        table.add(new StatisticLabelCell("Rx pkt", FIRST_COLUMN_WIDTH, hCol%2 == 0, CellType.DEFAULT_CELL, false), 0, hCol++);
        table.add(new StatisticLabelCell("Max Latency", FIRST_COLUMN_WIDTH, hCol%2 == 0, CellType.DEFAULT_CELL, false), 0, hCol++);
        table.add(new StatisticLabelCell("Avg Latency", FIRST_COLUMN_WIDTH, hCol%2 == 0, CellType.DEFAULT_CELL, false), 0, hCol++);
        table.add(new StatisticLabelCell("Last (max)", FIRST_COLUMN_WIDTH, hCol%2 == 0, CellType.DEFAULT_CELL, false), 0, hCol++);
        for (int i = 0; i < WINDOW_SIZE - 1; ++i) {
            table.add(new StatisticLabelCell(String.format("Last-%d", i + 1), FIRST_COLUMN_WIDTH, hCol%2 == 0, CellType.DEFAULT_CELL, false), 0, hCol++);
        }
        table.add(new StatisticLabelCell("Jitter", FIRST_COLUMN_WIDTH, hCol%2 == 0, CellType.DEFAULT_CELL, false), 0, hCol++);
        table.add(new StatisticLabelCell("Errors", FIRST_COLUMN_WIDTH, hCol%2 == 0, CellType.DEFAULT_CELL, false), 0, hCol);

        final Map<String, LatencyInfo> latencyInfoMap = LatencyStatsLoader.getInstance().getLatencyInfoMap();
        final Map<String, ArrayHistory<Number>> maxLatencyByStreams =
                LatencyStatsLoader.getInstance().getLatencyWindowHistoryMap();
        final Map<String, ArrayHistory<StatsFlowStream>> flowStatsMap = StatsLoader.getInstance().getFlowStatsHistoryMap();

        final AtomicInteger rowIndex = new AtomicInteger(1);
        synchronized (latencyInfoMap) {
            synchronized (maxLatencyByStreams) {
                synchronized (flowStatsMap) {
                    latencyInfoMap.forEach((String stream, LatencyInfo latencyInfo) -> {
                        final ArrayHistory<StatsFlowStream> flowStreamHistory = flowStatsMap.get(stream);
                        if (flowStreamHistory == null || flowStreamHistory.isEmpty()) {
                            return;
                        }

                        final StatsFlowStream flowStats = flowStreamHistory.last();
                        if (flowStats == null) {
                            return;
                        }

                        Number[] window = new Number[WINDOW_SIZE];
                        for (int i = 0; i < WINDOW_SIZE; ++i) {
                            window[i] = 0;
                        }
                        final ArrayHistory<Number> maxLatencyHistory = maxLatencyByStreams.get(stream);
                        if (maxLatencyHistory != null) {
                            final int historySize = maxLatencyHistory.size();
                            final int size = Math.min(historySize, WINDOW_SIZE);
                            for (int i = 0; i < size; i++) {
                                window[i] = maxLatencyHistory.get(historySize - 1 - i);
                            }
                        }

                        int col = 0;
                        table.add(new HeaderCell(COLUMN_WIDTH, stream), rowIndex.get(), col++);
                        table.add(new StatisticLabelCell(Util.getFormatted(String.valueOf(flowStats.calcTotalTxPkts()), true, "pkts"), COLUMN_WIDTH, col % 2 == 0, CellType.DEFAULT_CELL, true), rowIndex.get(), col++);
                        table.add(new StatisticLabelCell(Util.getFormatted(String.valueOf(flowStats.calcTotalRxPkts()), true, "pkts"), COLUMN_WIDTH, col % 2 == 0, CellType.DEFAULT_CELL, true), rowIndex.get(), col++);
                        table.add(new StatisticLabelCell(String.format("%d \u00B5s", latencyInfo.getTotalMax()), COLUMN_WIDTH, col % 2 == 0, CellType.DEFAULT_CELL, true), rowIndex.get(), col++);
                        table.add(new StatisticLabelCell(String.format(Locale.US, "%.2f \u00B5s", round(latencyInfo.getAverage())), COLUMN_WIDTH, col % 2 == 0, CellType.DEFAULT_CELL, true), rowIndex.get(), col++);
                        for (int i = 0; i < WINDOW_SIZE; ++i) {
                            table.add(new StatisticLabelCell(String.valueOf(window[i]), COLUMN_WIDTH, col % 2 == 0, CellType.DEFAULT_CELL, true), rowIndex.get(), col++);
                        }
                        table.add(new StatisticLabelCell(String.format("%d \u00B5s", latencyInfo.getJitter()), COLUMN_WIDTH, col % 2 == 0, CellType.DEFAULT_CELL, true), rowIndex.get(), col++);
                        table.add(new StatisticLabelCell(String.valueOf(latencyInfo.getTotalErrors()), COLUMN_WIDTH, true, CellType.ERROR_CELL, true), rowIndex.get(), col++);

                        rowIndex.addAndGet(1);
                    });
                }
            }
        }
    }

    private void renderHistogram() {
        table.getChildren().clear();

        final Map<String, LatencyInfo> latencyInfoMap = LatencyStatsLoader.getInstance().getLatencyInfoMap();
        final Map<String, Map<String, Long>> histogramMap = LatencyStatsLoader.getInstance().getHistogramMap();
        final String[] histogramKeys = LatencyStatsLoader.getInstance().getHistogramKeys(HISTOGRAM_SIZE);
        final Map<String, ArrayHistory<StatsFlowStream>> flowStatsMap = StatsLoader.getInstance().getFlowStatsHistoryMap();

        int hCol = 0;
        table.add(new HeaderCell(FIRST_COLUMN_WIDTH, "PG ID"), 0, hCol++);
        for (final String key : histogramKeys) {
            table.add(new StatisticLabelCell(key, FIRST_COLUMN_WIDTH, hCol%2 == 0, CellType.DEFAULT_CELL, false), 0, hCol++);
        }
        table.add(new StatisticLabelCell("Dropped", FIRST_COLUMN_WIDTH, hCol%2 == 0, CellType.DEFAULT_CELL, false), 0, hCol++);
        table.add(new StatisticLabelCell("Dup", FIRST_COLUMN_WIDTH, hCol%2 == 0, CellType.DEFAULT_CELL, false), 0, hCol++);
        table.add(new StatisticLabelCell("Out Of Order", FIRST_COLUMN_WIDTH, hCol%2 == 0, CellType.DEFAULT_CELL, false), 0, hCol++);
        table.add(new StatisticLabelCell("Seq To High", FIRST_COLUMN_WIDTH, hCol%2 == 0, CellType.DEFAULT_CELL, false), 0, hCol++);
        table.add(new StatisticLabelCell("Seq To Low", FIRST_COLUMN_WIDTH, hCol%2 == 0, CellType.DEFAULT_CELL, false), 0, hCol);

        final AtomicInteger rowIndex = new AtomicInteger(1);
        synchronized (latencyInfoMap) {
            synchronized (histogramMap) {
                synchronized (histogramKeys) {
                    synchronized (flowStatsMap) {
                        latencyInfoMap.forEach((final String stream, final LatencyInfo latencyInfo) -> {
                            final ArrayHistory<StatsFlowStream> flowStreamHistory = flowStatsMap.get(stream);
                            if (flowStreamHistory == null || flowStreamHistory.isEmpty()) {
                                return;
                            }

                            final Map<String, Long> histogram = histogramMap.get(stream);
                            if (histogram == null) {
                                return;
                            }

                            int col = 0;
                            table.add(new HeaderCell(COLUMN_WIDTH, stream), rowIndex.get(), col++);
                            for (final String key : histogramKeys) {
                                Long value = histogram.getOrDefault(key, 0L);
                                table.add(new StatisticLabelCell(String.valueOf(value), COLUMN_WIDTH, col % 2 == 0, CellType.DEFAULT_CELL, true), rowIndex.get(), col++);
                            }
                            table.add(new StatisticLabelCell(String.valueOf(latencyInfo.getDropped()), COLUMN_WIDTH, col % 2 == 0, CellType.ERROR_CELL, true), rowIndex.get(), col++);
                            table.add(new StatisticLabelCell(String.valueOf(latencyInfo.getDup()), COLUMN_WIDTH, col % 2 == 0, CellType.ERROR_CELL, true), rowIndex.get(), col++);
                            table.add(new StatisticLabelCell(String.valueOf(latencyInfo.getOutOfOrder()), COLUMN_WIDTH, col % 2 == 0, CellType.ERROR_CELL, true), rowIndex.get(), col++);
                            table.add(new StatisticLabelCell(String.valueOf(latencyInfo.getSeqTooHigh()), COLUMN_WIDTH, col % 2 == 0, CellType.ERROR_CELL, true), rowIndex.get(), col++);
                            table.add(new StatisticLabelCell(String.valueOf(latencyInfo.getSeqTooLow()), COLUMN_WIDTH, col % 2 == 0, CellType.ERROR_CELL, true), rowIndex.get(), col);

                            rowIndex.addAndGet(1);
                        });
                    }
                }
            }
        }
    }

    static double round(double value) {
        return ((int)(value*100))/100.0;
    }
}
