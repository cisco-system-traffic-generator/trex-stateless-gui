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
import com.exalttech.trex.ui.models.stats.latency.StatsLatencyStream;
import com.exalttech.trex.ui.models.stats.latency.StatsLatencyStreamLatency;
import com.exalttech.trex.ui.views.statistics.cells.CellType;
import com.exalttech.trex.ui.views.statistics.cells.HeaderCell;
import com.exalttech.trex.ui.views.statistics.cells.StatisticLabelCell;
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

    private Set<Integer> lastVisiblePorts;
    private Set<String> lastVisibleStreams;
    private int lastStreamsCount;

    public DashboardTabLatency() {
        Initialization.initializeFXML(this, "/fxml/Dashboard/tabs/latency/DashboardTabLatency.fxml");

        toggleGroupMode.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if (newValue == null) {
                    oldValue.setSelected(true);
                } else {
                    update(lastVisiblePorts, lastVisibleStreams, lastStreamsCount);
                }
            }
        });
    }

    public void update(Set<Integer> visiblePorts, Set<String> visibleStreams, int streamsCount) {
        if (((ToggleButton)toggleGroupMode.getSelectedToggle()).getText().equals("Window")) {
            renderWindow(visiblePorts, visibleStreams, streamsCount);
        } else {
            renderHistogram(visibleStreams, streamsCount);
        }

        this.lastVisiblePorts = visiblePorts;
        this.lastVisibleStreams = visibleStreams;
        this.lastStreamsCount = streamsCount;
    }

    private void renderWindow(Set<Integer> visiblePorts, Set<String> visibleStreams, int streamsCount) {
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

        final Map<String, StatsLatencyStream> latencyStatsByStreams = StatsLoader.getInstance().getLatencyStatsMap();
        final Map<String, ArrayHistory<Number>> maxLatencyByStreams = StatsLoader.getInstance().getLatencyWindowHistory();
        final Map<String, ArrayHistory<StatsFlowStream>> flowStatsMap = StatsLoader.getInstance().getFlowStatsHistoryMap();

        final AtomicInteger rowIndex = new AtomicInteger(1);
        latencyStatsByStreams.forEach((String stream, StatsLatencyStream latencyStream) -> {
            if (rowIndex.get() > streamsCount || (visibleStreams != null && !visibleStreams.contains(stream))) {
                return;
            }

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
            table.add(new StatisticLabelCell(Util.getFormatted(String.valueOf(flowStats.calcTotalTxPkts(visiblePorts)), true, "pkts"), COLUMN_WIDTH, col%2 == 0, CellType.DEFAULT_CELL, true), rowIndex.get(), col++);
            table.add(new StatisticLabelCell(Util.getFormatted(String.valueOf(flowStats.calcTotalRxPkts(visiblePorts)), true, "pkts"), COLUMN_WIDTH, col%2 == 0, CellType.DEFAULT_CELL, true), rowIndex.get(), col++);
            table.add(new StatisticLabelCell(String.format("%d \u00B5s", latencyStream.getLatency().getTotalMax()), COLUMN_WIDTH, col%2 == 0, CellType.DEFAULT_CELL, true), rowIndex.get(), col++);
            table.add(new StatisticLabelCell(String.format(Locale.US, "%.2f \u00B5s", round(latencyStream.getLatency().getAverage())), COLUMN_WIDTH, col%2 == 0, CellType.DEFAULT_CELL, true), rowIndex.get(), col++);
            for (int i = 0; i < WINDOW_SIZE; ++i) {
                table.add(new StatisticLabelCell(String.valueOf(window[i]), COLUMN_WIDTH, col%2 == 0, CellType.DEFAULT_CELL, true), rowIndex.get(), col++);
            }
            table.add(new StatisticLabelCell(String.format("%d \u00B5s", latencyStream.getLatency().getJitter()), COLUMN_WIDTH, col%2 == 0, CellType.DEFAULT_CELL, true), rowIndex.get(), col++);
            table.add(new StatisticLabelCell(String.valueOf(latencyStream.getErrCntrs().getTotal()), COLUMN_WIDTH, true, CellType.ERROR_CELL, true), rowIndex.get(), col++);

            rowIndex.addAndGet(1);
        });
    }

    private void renderHistogram(Set<String> visibleStreams, int streamsCount) {
        table.getChildren().clear();

        final Map<String, StatsLatencyStream> latencyStatsByStreams = StatsLoader.getInstance().getLatencyStatsMap();
        final Map<String, ArrayHistory<StatsFlowStream>> flowStatsMap = StatsLoader.getInstance().getFlowStatsHistoryMap();

        final TreeSet<Integer> keys = new TreeSet<>();
        final AtomicInteger statsIndex = new AtomicInteger(0);
        latencyStatsByStreams.forEach((final String stream, final StatsLatencyStream statslatencyStream) -> {
            if (statsIndex.get() >= streamsCount || (visibleStreams != null && !visibleStreams.contains(stream))) {
                return;
            }
            statslatencyStream.getLatency().getHistogram().keySet().forEach((final String key) -> {
                keys.add(Integer.parseInt(key));
            });
        });
        final int histogramSize = Math.min(keys.size(), HISTOGRAM_SIZE);
        final String[] keysOrder = new String[histogramSize];
        for (int i = 0; i < histogramSize; ++i) {
            keysOrder[i] = String.valueOf(keys.pollLast());
        }

        int hCol = 0;
        table.add(new HeaderCell(FIRST_COLUMN_WIDTH, "PG ID"), 0, hCol++);
        for (final String key : keysOrder) {
            table.add(new StatisticLabelCell(key, FIRST_COLUMN_WIDTH, hCol%2 == 0, CellType.DEFAULT_CELL, false), 0, hCol++);
        }
        table.add(new StatisticLabelCell("Dropped", FIRST_COLUMN_WIDTH, hCol%2 == 0, CellType.DEFAULT_CELL, false), 0, hCol++);
        table.add(new StatisticLabelCell("Dup", FIRST_COLUMN_WIDTH, hCol%2 == 0, CellType.DEFAULT_CELL, false), 0, hCol++);
        table.add(new StatisticLabelCell("Out Of Order", FIRST_COLUMN_WIDTH, hCol%2 == 0, CellType.DEFAULT_CELL, false), 0, hCol++);
        table.add(new StatisticLabelCell("Seq To High", FIRST_COLUMN_WIDTH, hCol%2 == 0, CellType.DEFAULT_CELL, false), 0, hCol++);
        table.add(new StatisticLabelCell("Seq To Low", FIRST_COLUMN_WIDTH, hCol%2 == 0, CellType.DEFAULT_CELL, false), 0, hCol);

        final AtomicInteger rowIndex = new AtomicInteger(1);
        latencyStatsByStreams.forEach((final String stream, final StatsLatencyStream latencyStream) -> {
            if (rowIndex.get() > streamsCount || (visibleStreams != null && !visibleStreams.contains(stream))) {
                return;
            }

            final ArrayHistory<StatsFlowStream> flowStreamHistory = flowStatsMap.get(stream);
            if (flowStreamHistory == null || flowStreamHistory.isEmpty()) {
                return;
            }

            final StatsLatencyStreamLatency latency = latencyStream.getLatency();
            if (latency == null) {
                return;
            }

            final Map<String, Integer> histogram = latency.getHistogram();
            if (histogram == null || histogram.isEmpty()) {
                return;
            }

            int col = 0;
            table.add(new HeaderCell(COLUMN_WIDTH, stream), rowIndex.get(), col++);
            for (final String key : keysOrder) {
                Integer value = histogram.get(String.valueOf(key));
                if (value == null) {
                    value = 0;
                }
                table.add(new StatisticLabelCell(String.valueOf(value), COLUMN_WIDTH, col%2 == 0, CellType.DEFAULT_CELL, true), rowIndex.get(), col++);
            }
            table.add(new StatisticLabelCell(String.valueOf(latencyStream.getErrCntrs().getDropped()), COLUMN_WIDTH, col%2 == 0, CellType.ERROR_CELL, true), rowIndex.get(), col++);
            table.add(new StatisticLabelCell(String.valueOf(latencyStream.getErrCntrs().getDup()), COLUMN_WIDTH, col%2 == 0, CellType.ERROR_CELL, true), rowIndex.get(), col++);
            table.add(new StatisticLabelCell(String.valueOf(latencyStream.getErrCntrs().getOutOfOrder()), COLUMN_WIDTH, col%2 == 0, CellType.ERROR_CELL, true), rowIndex.get(), col++);
            table.add(new StatisticLabelCell(String.valueOf(latencyStream.getErrCntrs().getSeqTooHigh()), COLUMN_WIDTH, col%2 == 0, CellType.ERROR_CELL, true), rowIndex.get(), col++);
            table.add(new StatisticLabelCell(String.valueOf(latencyStream.getErrCntrs().getSeqTooLow()), COLUMN_WIDTH, col%2 == 0, CellType.ERROR_CELL, true), rowIndex.get(), col);

            rowIndex.addAndGet(1);
        });
    }

    static double round(double value) {
        return ((int)(value*100))/100.0;
    }
}
