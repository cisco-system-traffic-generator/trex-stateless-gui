package com.cisco.trex.stl.gui.controllers.dashboard.latency;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.WindowEvent;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.cisco.trex.stateless.model.stats.LatencyStat;
import com.cisco.trex.stateless.model.stats.LatencyStatErr;
import com.cisco.trex.stateless.model.stats.LatencyStatLat;

import com.cisco.trex.stl.gui.controllers.dashboard.FlowStatsAnchorPane;
import com.cisco.trex.stl.gui.models.FlowStatPoint;
import com.cisco.trex.stl.gui.models.LatencyStatPoint;
import com.cisco.trex.stl.gui.storages.PGIDStatsStorage;
import com.cisco.trex.stl.gui.storages.StatsStorage;

import com.exalttech.trex.ui.views.statistics.cells.CellType;
import com.exalttech.trex.ui.views.statistics.cells.HeaderCell;
import com.exalttech.trex.ui.views.statistics.cells.StatisticLabelCell;
import com.exalttech.trex.util.ArrayHistory;
import com.exalttech.trex.util.Initialization;
import com.exalttech.trex.util.Util;


public class Latency extends FlowStatsAnchorPane {
    private static final int FIRST_COLUMN_WIDTH = 120;
    private static final int COLUMN_WIDTH = 150;
    private static final int WINDOW_SIZE = 10;
    private static final int HISTOGRAM_SIZE = 11;

    @FXML
    private AnchorPane root;
    @FXML
    private ToggleGroup toggleGroupMode;
    @FXML
    private GridPane table;

    public Latency() {
        Initialization.initializeFXML(this, "/fxml/dashboard/latency/Latency.fxml");
        Initialization.initializeCloseEvent(root, this::onWindowCloseRequest);

        toggleGroupMode.selectedToggleProperty().addListener(this::typeChanged);
    }

    private void onWindowCloseRequest(final WindowEvent window) {
        setActive(false);
    }

    private void typeChanged(
            final ObservableValue<? extends Toggle> observable,
            final Toggle oldValue,
            final Toggle newValue
    ) {
        if (newValue == null) {
            oldValue.setSelected(true);
        } else {
            render();
        }
    }

    @Override
    protected void render() {
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

        final PGIDStatsStorage pgIDStatsStorage = StatsStorage.getInstance().getPGIDStatsStorage();
        final Map<Integer, ArrayHistory<FlowStatPoint>> flowStatPointHistoryMap =
                pgIDStatsStorage.getFlowStatPointHistoryMap();
        final Map<Integer, FlowStatPoint> flowStatPointShadowMap =
                pgIDStatsStorage.getFlowStatPointShadowMap();
        final Map<Integer, ArrayHistory<LatencyStatPoint>> latencyStatPointHistoryMap =
                pgIDStatsStorage.getLatencyStatPointHistoryMap();
        final Map<Integer, LatencyStatPoint> latencyStatPointShadowMap =
                pgIDStatsStorage.getLatencyStatPointShadowMap();

        int rowIndex = 1;
        synchronized (pgIDStatsStorage.getDataLock()) {
            for (final Map.Entry<Integer, ArrayHistory<FlowStatPoint>> entry : flowStatPointHistoryMap.entrySet()) {
                final int pgID = entry.getKey();

                final ArrayHistory<FlowStatPoint> flowHistory = entry.getValue();
                if (flowHistory == null || flowHistory.isEmpty()) {
                    continue;
                }
                final FlowStatPoint flowStatPoint = flowHistory.last();

                final ArrayHistory<LatencyStatPoint> latencyHistory = latencyStatPointHistoryMap.get(pgID);
                if (latencyHistory == null || latencyHistory.isEmpty()) {
                    continue;
                }
                final LatencyStat latencyStat = latencyHistory.last().getLatencyStat();

                final long[] window = new long[WINDOW_SIZE];
                for (int i = 0; i < WINDOW_SIZE; ++i) {
                    window[i] = 0;
                }
                final int latencyHistorySize = latencyHistory.size();
                final int size = Math.min(latencyHistorySize, WINDOW_SIZE);
                for (int i = 0; i < size; i++) {
                    window[i] = latencyHistory
                            .get(latencyHistorySize - 1 - i)
                            .getLatencyStat()
                            .getLat()
                            .getLastMax();
                }

                long tp = flowStatPoint.getTp();
                long rp = flowStatPoint.getRp();

                final FlowStatPoint flowShadow = flowStatPointShadowMap.get(pgID);
                if (flowShadow != null) {
                    tp -= flowShadow.getTp();
                    rp -= flowShadow.getRp();
                }

                long totalErr = latencyStat.getErr().getTotal();

                final LatencyStatPoint latencyShadow = latencyStatPointShadowMap.get(pgID);
                if (latencyShadow != null) {
                    totalErr -= latencyShadow.getLatencyStat().getErr().getTotal();
                }

                final LatencyStatLat lat = latencyStat.getLat();

                int col = 0;
                table.add(new HeaderCell(COLUMN_WIDTH, String.valueOf(pgID)), rowIndex, col++);
                table.add(new StatisticLabelCell(Util.getFormatted(String.valueOf(tp), true, "pkts"), COLUMN_WIDTH, col % 2 == 0, CellType.DEFAULT_CELL, true), rowIndex, col++);
                table.add(new StatisticLabelCell(Util.getFormatted(String.valueOf(rp), true, "pkts"), COLUMN_WIDTH, col % 2 == 0, CellType.DEFAULT_CELL, true), rowIndex, col++);
                table.add(new StatisticLabelCell(String.format("%d \u00B5s", lat.getTotalMax()), COLUMN_WIDTH, col % 2 == 0, CellType.DEFAULT_CELL, true), rowIndex, col++);
                table.add(new StatisticLabelCell(String.format(Locale.US, "%.2f \u00B5s", round(lat.getAverage())), COLUMN_WIDTH, col % 2 == 0, CellType.DEFAULT_CELL, true), rowIndex, col++);
                for (int i = 0; i < WINDOW_SIZE; ++i) {
                    table.add(new StatisticLabelCell(String.valueOf(window[i]), COLUMN_WIDTH, col % 2 == 0, CellType.DEFAULT_CELL, true), rowIndex, col++);
                }
                table.add(new StatisticLabelCell(String.format("%d \u00B5s", lat.getJit()), COLUMN_WIDTH, col % 2 == 0, CellType.DEFAULT_CELL, true), rowIndex, col++);
                table.add(new StatisticLabelCell(String.valueOf(totalErr), COLUMN_WIDTH, true, CellType.ERROR_CELL, true), rowIndex, col++);

                rowIndex++;
            }
        }
    }

    private void renderHistogram() {
        table.getChildren().clear();

        final PGIDStatsStorage pgIDStatsStorage = StatsStorage.getInstance().getPGIDStatsStorage();
        final Map<Integer, ArrayHistory<LatencyStatPoint>> latencyStatPointHistoryMap =
                pgIDStatsStorage.getLatencyStatPointHistoryMap();
        final Map<Integer, LatencyStatPoint> latencyStatPointShadowMap =
                pgIDStatsStorage.getLatencyStatPointShadowMap();
        final String[] histogramKeys = pgIDStatsStorage.getHistogramKeys(HISTOGRAM_SIZE);

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

        int rowIndex = 1;
        synchronized (pgIDStatsStorage.getDataLock()) {
            for (final Map.Entry<Integer, ArrayHistory<LatencyStatPoint>> entry : latencyStatPointHistoryMap.entrySet()) {
                final int pgID = entry.getKey();

                final ArrayHistory<LatencyStatPoint> latencyHistory = latencyStatPointHistoryMap.get(pgID);
                if (latencyHistory == null || latencyHistory.isEmpty()) {
                    continue;
                }
                final LatencyStat latencyStat = latencyHistory.last().getLatencyStat();

                final Map<String, Long> histogram = latencyStat.getLat().getHistogram();

                final LatencyStatErr latencyStatErr = latencyStat.getErr();
                long drp = latencyStatErr.getDrp();
                long dup = latencyStatErr.getDup();
                long ooo = latencyStatErr.getOoo();
                long sth = latencyStatErr.getSth();
                long stl = latencyStatErr.getStl();

                final LatencyStatPoint latencyShadow = latencyStatPointShadowMap.get(pgID);
                Map<String, Long> shadowHistogram;
                if (latencyShadow != null) {
                    final LatencyStatErr latencyStatShadowErr = latencyShadow.getLatencyStat().getErr();
                    drp -= latencyStatShadowErr.getDrp();
                    dup -= latencyStatShadowErr.getDup();
                    ooo -= latencyStatShadowErr.getOoo();
                    sth -= latencyStatShadowErr.getSth();
                    stl -= latencyStatShadowErr.getStl();

                    shadowHistogram = latencyShadow.getLatencyStat().getLat().getHistogram();
                } else {
                    shadowHistogram = new HashMap<>();
                }

                int col = 0;
                table.add(new HeaderCell(COLUMN_WIDTH, String.valueOf(pgID)), rowIndex, col++);
                for (final String key : histogramKeys) {
                    final long value = histogram.getOrDefault(key, 0L);
                    final long shadowValue = shadowHistogram.getOrDefault(key, 0L);
                    table.add(new StatisticLabelCell(String.valueOf(value - shadowValue), COLUMN_WIDTH, col % 2 == 0, CellType.DEFAULT_CELL, true), rowIndex, col++);
                }
                table.add(new StatisticLabelCell(String.valueOf(drp), COLUMN_WIDTH, col % 2 == 0, CellType.ERROR_CELL, true), rowIndex, col++);
                table.add(new StatisticLabelCell(String.valueOf(dup), COLUMN_WIDTH, col % 2 == 0, CellType.ERROR_CELL, true), rowIndex, col++);
                table.add(new StatisticLabelCell(String.valueOf(ooo), COLUMN_WIDTH, col % 2 == 0, CellType.ERROR_CELL, true), rowIndex, col++);
                table.add(new StatisticLabelCell(String.valueOf(sth), COLUMN_WIDTH, col % 2 == 0, CellType.ERROR_CELL, true), rowIndex, col++);
                table.add(new StatisticLabelCell(String.valueOf(stl), COLUMN_WIDTH, col % 2 == 0, CellType.ERROR_CELL, true), rowIndex, col);

                rowIndex++;
            }
        }
    }

    static double round(double value) {
        return ((int)(value*100))/100.0;
    }
}
