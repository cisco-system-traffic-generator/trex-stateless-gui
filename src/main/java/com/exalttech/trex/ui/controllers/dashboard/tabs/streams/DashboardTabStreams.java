package com.exalttech.trex.ui.controllers.dashboard.tabs.streams;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.util.*;

import com.exalttech.trex.ui.models.stats.flow.StatsFlowStream;
import com.exalttech.trex.ui.views.statistics.StatsLoader;
import com.exalttech.trex.ui.views.statistics.cells.CellType;
import com.exalttech.trex.ui.views.statistics.cells.HeaderCell;
import com.exalttech.trex.ui.views.statistics.cells.StatisticLabelCell;
import com.exalttech.trex.util.ArrayHistory;
import com.exalttech.trex.util.Initialization;
import com.exalttech.trex.util.Util;


public class DashboardTabStreams extends AnchorPane {
    @FXML
    private GridPane table;

    public DashboardTabStreams() {
        Initialization.initializeFXML(this, "/fxml/Dashboard/tabs/streams/DashboardTabStreams.fxml");
    }

    public void update(final Map<Integer, String> selectedPGIds) {
        StatsLoader statsLoader = StatsLoader.getInstance();
        Map<String, ArrayHistory<StatsFlowStream>> streams = statsLoader.getFlowStatsHistoryMap();

        int firstColumnWidth = 120;
        int secondHeaderWidth = 150;

        table.getChildren().clear();

        table.add(new HeaderCell(firstColumnWidth, "PG ID"), 0, 0);
        table.add(new StatisticLabelCell("Tx pps", firstColumnWidth, false, CellType.DEFAULT_CELL, false), 0, 1);
        table.add(new StatisticLabelCell("Tx bps L2", firstColumnWidth, true, CellType.DEFAULT_CELL, false), 0, 2);
        table.add(new StatisticLabelCell("Tx bps L1", firstColumnWidth, false, CellType.DEFAULT_CELL, false), 0, 3);
        table.add(new StatisticLabelCell("Rx pps", firstColumnWidth, true, CellType.DEFAULT_CELL, false), 0, 4);
        table.add(new StatisticLabelCell("Rx bps L2", firstColumnWidth, false, CellType.DEFAULT_CELL, false), 0, 5);
        table.add(new StatisticLabelCell("Tx pkts", firstColumnWidth, true, CellType.DEFAULT_CELL, false), 0, 6);
        table.add(new StatisticLabelCell("Rx pkts", firstColumnWidth, false, CellType.DEFAULT_CELL, false), 0, 7);
        table.add(new StatisticLabelCell("Tx bytes", firstColumnWidth, true, CellType.DEFAULT_CELL, false), 0, 8);
        table.add(new StatisticLabelCell("Rx bytes", firstColumnWidth, false, CellType.DEFAULT_CELL, false), 0, 9);

        int rowIndex = 1;

        synchronized (streams) {
            for (final Integer pgid : selectedPGIds.keySet()) {
                final String stream = String.valueOf(pgid);
                final ArrayHistory<StatsFlowStream> history = streams.get(String.valueOf(stream));
                if (history == null || history.isEmpty()) {
                    continue;
                }

                StatsFlowStream last = history.last();
                table.add(new HeaderCell(secondHeaderWidth, stream), rowIndex, 0);
                table.add(new StatisticLabelCell(Util.getFormatted(String.valueOf(round(last.calcTotalTxPps())), true, "pkt/s"), secondHeaderWidth, false, CellType.DEFAULT_CELL, true), rowIndex, 1);
                table.add(new StatisticLabelCell(Util.getFormatted(String.valueOf(round(last.calcTotalTxBpsL2())), true, "b/s"), secondHeaderWidth, true, CellType.DEFAULT_CELL, true), rowIndex, 2);
                table.add(new StatisticLabelCell(Util.getFormatted(String.valueOf(round(last.calcTotalTxBpsL1())), true, "b/s"), secondHeaderWidth, false, CellType.DEFAULT_CELL, true), rowIndex, 3);
                table.add(new StatisticLabelCell(Util.getFormatted(String.valueOf(round(last.calcTotalRxPps())), true, "pkt/s"), secondHeaderWidth, true, CellType.DEFAULT_CELL, true), rowIndex, 4);
                table.add(new StatisticLabelCell(Util.getFormatted(String.valueOf(round(last.calcTotalRxBps())), true, "b/s"), secondHeaderWidth, false, CellType.DEFAULT_CELL, true), rowIndex, 5);
                table.add(new StatisticLabelCell(Util.getFormatted(String.valueOf(last.calcTotalTxPkts()), true, "pkts"), secondHeaderWidth, true, CellType.DEFAULT_CELL, true), rowIndex, 6);
                table.add(new StatisticLabelCell(Util.getFormatted(String.valueOf(last.calcTotalRxPkts()), true, "pkts"), secondHeaderWidth, false, CellType.DEFAULT_CELL, true), rowIndex, 7);
                table.add(new StatisticLabelCell(Util.getFormatted(String.valueOf(last.calcTotalTxBytes()), true, "B"), secondHeaderWidth, true, CellType.DEFAULT_CELL, true), rowIndex, 8);
                table.add(new StatisticLabelCell(Util.getFormatted(String.valueOf(last.calcTotalRxBytes()), true, "B"), secondHeaderWidth, false, CellType.DEFAULT_CELL, true), rowIndex, 9);

                rowIndex++;
            }
        }
    }

    static double round(double value) {
        return ((int)(value*100))/100.0;
    }
}
