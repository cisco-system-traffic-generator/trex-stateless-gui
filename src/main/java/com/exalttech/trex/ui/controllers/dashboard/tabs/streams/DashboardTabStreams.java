package com.exalttech.trex.ui.controllers.dashboard.tabs.streams;

import com.exalttech.trex.ui.models.stats.flow.StatsFlowStream;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.exalttech.trex.ui.views.statistics.StatsLoader;
import com.exalttech.trex.ui.views.statistics.cells.CellType;
import com.exalttech.trex.ui.views.statistics.cells.HeaderCell;
import com.exalttech.trex.ui.views.statistics.cells.StatisticLabelCell;
import com.exalttech.trex.util.ArrayHistory;
import com.exalttech.trex.util.Initialization;


public class DashboardTabStreams extends AnchorPane {
    @FXML
    private GridPane table;

    public DashboardTabStreams() {
        Initialization.initializeFXML(this, "/fxml/Dashboard/tabs/streams/DashboardTabStreams.fxml");
    }

    public void update(Set<Integer> visiblePorts, Set<String> visibleStreams) {
        StatsLoader statsLoader = StatsLoader.getInstance();
        Map<String, ArrayHistory<StatsFlowStream>> streams = statsLoader.getFlowStatsHistoryMap();

        int firstColumnWidth = 99;
        int secondHeaderWidth = 128;

        table.getChildren().clear();

        table.add(new HeaderCell(firstColumnWidth, "PG ID"), 0, 0);
        table.add(new HeaderCell(secondHeaderWidth, "Tx (pkt/s)"), 1, 0);
        table.add(new HeaderCell(secondHeaderWidth, "Tx (B/s) L2"), 2, 0);
        table.add(new HeaderCell(secondHeaderWidth, "Tx (B/s) L1"), 3, 0);
        table.add(new HeaderCell(secondHeaderWidth, "Rx (pkt/s)"), 4, 0);
        table.add(new HeaderCell(secondHeaderWidth, "Rx (B/s)"), 5, 0);
        table.add(new HeaderCell(secondHeaderWidth, "Tx (pkt)"), 6, 0);
        table.add(new HeaderCell(secondHeaderWidth, "Rx (pkt)"), 7, 0);
        table.add(new HeaderCell(secondHeaderWidth, "Tx (B)"), 8, 0);
        table.add(new HeaderCell(secondHeaderWidth, "Rx (B)"), 9, 0);

        AtomicInteger rowIndex = new AtomicInteger(1);
        AtomicBoolean odd = new AtomicBoolean(false);

        streams.forEach((String stream, ArrayHistory<StatsFlowStream> history) -> {
            if ((visibleStreams != null && !visibleStreams.contains(stream)) || history.isEmpty()) {
                return;
            }

            StatsFlowStream last = history.last();
            table.add(new StatisticLabelCell(stream, firstColumnWidth, odd.get(), CellType.DEFAULT_CELL, true), 0, rowIndex.get());
            table.add(new StatisticLabelCell(String.valueOf(last.calcTotalTxPps(visiblePorts)), secondHeaderWidth, odd.get(), CellType.DEFAULT_CELL, true), 1, rowIndex.get());
            table.add(new StatisticLabelCell(String.valueOf(last.calcTotalTxBpsL2(visiblePorts)), secondHeaderWidth, odd.get(), CellType.DEFAULT_CELL, true), 2, rowIndex.get());
            table.add(new StatisticLabelCell(String.valueOf(last.calcTotalTxBpsL1(visiblePorts)), secondHeaderWidth, odd.get(), CellType.DEFAULT_CELL, true), 3, rowIndex.get());
            table.add(new StatisticLabelCell(String.valueOf(last.calcTotalRxPps(visiblePorts)), secondHeaderWidth, odd.get(), CellType.DEFAULT_CELL, true), 4, rowIndex.get());
            table.add(new StatisticLabelCell(String.valueOf(last.calcTotalRxBps(visiblePorts)), secondHeaderWidth, odd.get(), CellType.DEFAULT_CELL, true), 5, rowIndex.get());
            table.add(new StatisticLabelCell(String.valueOf(last.calcTotalTxPkts(visiblePorts)), secondHeaderWidth, odd.get(), CellType.DEFAULT_CELL, true), 6, rowIndex.get());
            table.add(new StatisticLabelCell(String.valueOf(last.calcTotalRxPkts(visiblePorts)), secondHeaderWidth, odd.get(), CellType.DEFAULT_CELL, true), 7, rowIndex.get());
            table.add(new StatisticLabelCell(String.valueOf(last.calcTotalTxBytes(visiblePorts)), secondHeaderWidth, odd.get(), CellType.DEFAULT_CELL, true), 8, rowIndex.get());
            table.add(new StatisticLabelCell(String.valueOf(last.calcTotalRxBytes(visiblePorts)), secondHeaderWidth, odd.get(), CellType.DEFAULT_CELL, true), 9, rowIndex.get());

            rowIndex.addAndGet(1);
            odd.getAndSet(!odd.get());
        });
    }
}
