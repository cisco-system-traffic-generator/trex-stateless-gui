package com.exalttech.trex.ui.controllers.dashboard.tabs.latency;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import com.exalttech.trex.ui.models.stats.flow.StatsFlowStream;
import com.exalttech.trex.ui.models.stats.latency.StatsLatencyStream;
import com.exalttech.trex.ui.views.statistics.cells.CellType;
import com.exalttech.trex.ui.views.statistics.cells.HeaderCell;
import com.exalttech.trex.ui.views.statistics.cells.StatisticLabelCell;
import com.exalttech.trex.ui.views.statistics.StatsLoader;
import com.exalttech.trex.util.ArrayHistory;
import com.exalttech.trex.util.Initialization;


public class DashboardTabLatency extends AnchorPane {
    @FXML
    private GridPane table;

    public DashboardTabLatency() {
        Initialization.initializeFXML(this, "/fxml/Dashboard/tabs/latency/DashboardTabLatency.fxml");
    }

    public void update(Set<Integer> visiblePorts, Set<String> visibleStreams) {
        int firstColumnWidth = 120;
        int secondHeaderWidth = 150;

        table.getChildren().clear();

        table.add(new HeaderCell(firstColumnWidth, "PG ID"), 0, 0);
        table.add(new StatisticLabelCell("Tx pkt", firstColumnWidth, false, CellType.DEFAULT_CELL, false), 0, 1);
        table.add(new StatisticLabelCell("Rx pkt", firstColumnWidth, true, CellType.DEFAULT_CELL, false), 0, 2);
        table.add(new StatisticLabelCell("Max Latency", firstColumnWidth, false, CellType.DEFAULT_CELL, false), 0, 3);
        table.add(new StatisticLabelCell("Avg Latency", firstColumnWidth, true, CellType.DEFAULT_CELL, false), 0, 4);
        table.add(new StatisticLabelCell("Jitter", firstColumnWidth, false, CellType.DEFAULT_CELL, false), 0, 5);
        table.add(new StatisticLabelCell("Dropped", firstColumnWidth, true, CellType.DEFAULT_CELL, false), 0, 6);
        table.add(new StatisticLabelCell("Dup", firstColumnWidth, false, CellType.DEFAULT_CELL, false), 0, 7);
        table.add(new StatisticLabelCell("Out Of Order", firstColumnWidth, true, CellType.DEFAULT_CELL, false), 0, 8);
        table.add(new StatisticLabelCell("Seq To High", firstColumnWidth, false, CellType.DEFAULT_CELL, false), 0, 9);
        table.add(new StatisticLabelCell("Seq To Low", firstColumnWidth, true, CellType.DEFAULT_CELL, false), 0, 10);

        Map<String, StatsLatencyStream> latencyStatsByStreams = StatsLoader.getInstance().getLatencyStatsMap();
        Map<String, ArrayHistory<StatsFlowStream>> flowStatsMap = StatsLoader.getInstance().getFlowStatsHistoryMap();

        AtomicInteger rowIndex = new AtomicInteger(1);
        latencyStatsByStreams.forEach((String stream, StatsLatencyStream latencyStream) -> {
            if (visibleStreams != null && !visibleStreams.contains(stream)) {
                return;
            }

            ArrayHistory<StatsFlowStream> flowStreamHistory = flowStatsMap.get(stream);
            if (flowStreamHistory == null || flowStreamHistory.isEmpty()) {
                return;
            }

            StatsFlowStream flowStats = flowStreamHistory.last();
            if (flowStats == null) {
                return;
            }

            table.add(new HeaderCell(secondHeaderWidth, stream), rowIndex.get(), 0);
            table.add(new StatisticLabelCell(String.valueOf(flowStats.calcTotalTxPkts(visiblePorts)), secondHeaderWidth, false, CellType.DEFAULT_CELL, true), rowIndex.get(), 1);
            table.add(new StatisticLabelCell(String.valueOf(flowStats.calcTotalRxPkts(visiblePorts)), secondHeaderWidth, true, CellType.DEFAULT_CELL, true), rowIndex.get(), 2);
            table.add(new StatisticLabelCell(String.valueOf(latencyStream.getLatency().getTotalMax()), secondHeaderWidth, false, CellType.DEFAULT_CELL, true), rowIndex.get(), 3);
            table.add(new StatisticLabelCell(String.valueOf(latencyStream.getLatency().getAverage()), secondHeaderWidth, true, CellType.DEFAULT_CELL, true), rowIndex.get(), 4);
            table.add(new StatisticLabelCell(String.valueOf(latencyStream.getLatency().getJitter()), secondHeaderWidth, false, CellType.DEFAULT_CELL, true), rowIndex.get(), 5);
            table.add(new StatisticLabelCell(String.valueOf(latencyStream.getErrCntrs().getDropped()), secondHeaderWidth, true, CellType.DEFAULT_CELL, true), rowIndex.get(), 6);
            table.add(new StatisticLabelCell(String.valueOf(latencyStream.getErrCntrs().getDup()), secondHeaderWidth, false, CellType.DEFAULT_CELL, true), rowIndex.get(), 7);
            table.add(new StatisticLabelCell(String.valueOf(latencyStream.getErrCntrs().getOutOfOrder()), secondHeaderWidth, true, CellType.DEFAULT_CELL, true), rowIndex.get(), 8);
            table.add(new StatisticLabelCell(String.valueOf(latencyStream.getErrCntrs().getSeqTooHigh()), secondHeaderWidth, false, CellType.DEFAULT_CELL, true), rowIndex.get(), 9);
            table.add(new StatisticLabelCell(String.valueOf(latencyStream.getErrCntrs().getSeqTooLow()), secondHeaderWidth, true, CellType.DEFAULT_CELL, true), rowIndex.get(), 10);

            rowIndex.addAndGet(1);
        });
    }
}
