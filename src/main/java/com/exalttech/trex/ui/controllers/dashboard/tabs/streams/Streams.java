package com.exalttech.trex.ui.controllers.dashboard.tabs.streams;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.WindowEvent;

import java.util.*;

import com.exalttech.trex.ui.controllers.dashboard.FlowStatsAnchorPane;
import com.exalttech.trex.ui.models.stats.FlowStatPoint;
import com.exalttech.trex.ui.views.statistics.cells.CellType;
import com.exalttech.trex.ui.views.statistics.cells.HeaderCell;
import com.exalttech.trex.ui.views.statistics.cells.StatisticLabelCell;
import com.exalttech.trex.ui.views.storages.PGIDStatsStorage;
import com.exalttech.trex.ui.views.storages.StatsStorage;
import com.exalttech.trex.util.ArrayHistory;
import com.exalttech.trex.util.Initialization;
import com.exalttech.trex.util.Util;


public class Streams extends FlowStatsAnchorPane {
    @FXML
    private AnchorPane root;
    @FXML
    private GridPane table;

    public Streams() {
        Initialization.initializeFXML(this, "/fxml/Dashboard/tabs/streams/Streams.fxml");
        Initialization.initializeCloseEvent(root, this::onWindowCloseRequest);
    }

    private void onWindowCloseRequest(final WindowEvent window) {
        setActive(false);
    }

    @Override
    protected void render() {
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

        final PGIDStatsStorage pgIDStatsStorage = StatsStorage.getInstance().getPGIDStatsStorage();
        final Map<Integer, ArrayHistory<FlowStatPoint>> flowStatPointHistoryMap =
                pgIDStatsStorage.getFlowStatPointHistoryMap();
        final Map<Integer, FlowStatPoint> flowStatPointShadowMap =
                pgIDStatsStorage.getFlowStatPointShadowMap();

        synchronized (pgIDStatsStorage.getFlowLock()) {
            for (final Map.Entry<Integer, ArrayHistory<FlowStatPoint>> entry : flowStatPointHistoryMap.entrySet()) {
                final int pgID = entry.getKey();
                final ArrayHistory<FlowStatPoint> history = entry.getValue();
                if (history == null || history.isEmpty()) {
                    continue;
                }

                final FlowStatPoint flowStatPoint = history.last();

                long tp = flowStatPoint.getTp();
                long rp = flowStatPoint.getRp();
                long tb = flowStatPoint.getTb();
                long rb = flowStatPoint.getRb();

                final FlowStatPoint shadow = flowStatPointShadowMap.get(pgID);
                if (shadow != null) {
                    tp -= shadow.getTp();
                    rp -= shadow.getRp();
                    tb -= shadow.getTb();
                    rb -= shadow.getRb();
                }

                table.add(new HeaderCell(secondHeaderWidth, String.valueOf(pgID)), rowIndex, 0);
                table.add(new StatisticLabelCell(Util.getFormatted(String.valueOf(round(flowStatPoint.getTps())), true, "pkt/s"), secondHeaderWidth, false, CellType.DEFAULT_CELL, true), rowIndex, 1);
                table.add(new StatisticLabelCell(Util.getFormatted(String.valueOf(round(flowStatPoint.getTbsL2())), true, "b/s"), secondHeaderWidth, true, CellType.DEFAULT_CELL, true), rowIndex, 2);
                table.add(new StatisticLabelCell(Util.getFormatted(String.valueOf(round(flowStatPoint.getTbsL1())), true, "b/s"), secondHeaderWidth, false, CellType.DEFAULT_CELL, true), rowIndex, 3);
                table.add(new StatisticLabelCell(Util.getFormatted(String.valueOf(round(flowStatPoint.getRps())), true, "pkt/s"), secondHeaderWidth, true, CellType.DEFAULT_CELL, true), rowIndex, 4);
                table.add(new StatisticLabelCell(Util.getFormatted(String.valueOf(round(flowStatPoint.getRbsL2())), true, "b/s"), secondHeaderWidth, false, CellType.DEFAULT_CELL, true), rowIndex, 5);
                table.add(new StatisticLabelCell(Util.getFormatted(String.valueOf(tp), true, "pkts"), secondHeaderWidth, true, CellType.DEFAULT_CELL, true), rowIndex, 6);
                table.add(new StatisticLabelCell(Util.getFormatted(String.valueOf(rp), true, "pkts"), secondHeaderWidth, false, CellType.DEFAULT_CELL, true), rowIndex, 7);
                table.add(new StatisticLabelCell(Util.getFormatted(String.valueOf(tb), true, "B"), secondHeaderWidth, true, CellType.DEFAULT_CELL, true), rowIndex, 8);
                table.add(new StatisticLabelCell(Util.getFormatted(String.valueOf(rb), true, "B"), secondHeaderWidth, false, CellType.DEFAULT_CELL, true), rowIndex, 9);

                rowIndex++;
            }
        }
    }

    static double round(double value) {
        return ((int)(value*100))/100.0;
    }
}
