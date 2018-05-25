package com.cisco.trex.stl.gui.controllers.dashboard.streams;

import com.cisco.trex.stl.gui.controllers.dashboard.FlowStatsBaseController;
import com.cisco.trex.stl.gui.controllers.dashboard.selectors.streams.StreamsSelectorController;
import com.cisco.trex.stl.gui.models.FlowStatPoint;
import com.cisco.trex.stl.gui.storages.PGIDStatsStorage;
import com.cisco.trex.stl.gui.storages.StatsStorage;
import com.exalttech.trex.ui.views.statistics.cells.CellType;
import com.exalttech.trex.ui.views.statistics.cells.HeaderCell;
import com.exalttech.trex.ui.views.statistics.cells.StatisticLabelCell;
import com.exalttech.trex.util.ArrayHistory;
import com.exalttech.trex.util.Initialization;
import com.exalttech.trex.util.Util;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.WindowEvent;

import java.util.Map;
import java.util.Set;


public class StreamsController extends FlowStatsBaseController {
    @FXML
    private AnchorPane root;
    @FXML
    private StreamsSelectorController streamSelector;
    @FXML
    private GridPane table;

    public StreamsController() {
        Initialization.initializeFXML(this, "/fxml/dashboard/streams/Streams.fxml");
        Initialization.initializeCloseEvent(root, this::onWindowCloseRequest);
    }

    @Override
    public void setActive(final boolean isActive) {
        super.setActive(isActive);
        streamSelector.setActive(isActive);
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
        table.add(new StatisticLabelCell("Rx bps L1", firstColumnWidth, true, CellType.DEFAULT_CELL, false), 0, 6);
        table.add(new StatisticLabelCell("Tx pkts", firstColumnWidth, false, CellType.DEFAULT_CELL, false), 0, 7);
        table.add(new StatisticLabelCell("Rx pkts", firstColumnWidth, true, CellType.DEFAULT_CELL, false), 0, 8);
        table.add(new StatisticLabelCell("Tx bytes", firstColumnWidth, false, CellType.DEFAULT_CELL, false), 0, 9);
        table.add(new StatisticLabelCell("Rx bytes", firstColumnWidth, true, CellType.DEFAULT_CELL, false), 0, 10);

        int rowIndex = 1;

        final PGIDStatsStorage pgIDStatsStorage = StatsStorage.getInstance().getPGIDStatsStorage();
        final Map<Integer, ArrayHistory<FlowStatPoint>> flowStatPointHistoryMap =
                pgIDStatsStorage.getFlowStatPointHistoryMap();
        final Map<Integer, FlowStatPoint> flowStatPointShadowMap =
                pgIDStatsStorage.getFlowStatPointShadowMap();
        final Set<Integer> stoppedPGIds = pgIDStatsStorage.getStoppedPGIds();

        synchronized (pgIDStatsStorage.getDataLock()) {
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

                final boolean isStopped = stoppedPGIds.contains(pgID);

                table.add(new HeaderCell(secondHeaderWidth, String.valueOf(pgID), isStopped), rowIndex, 0);
                table.add(new StatisticLabelCell(Util.getFormatted(String.valueOf(round(flowStatPoint.getTps())), true, "pkt/s"), secondHeaderWidth, false, CellType.DEFAULT_CELL, true, isStopped), rowIndex, 1);
                table.add(new StatisticLabelCell(Util.getFormatted(String.valueOf(round(flowStatPoint.getTbsL2())), true, "b/s"), secondHeaderWidth, true, CellType.DEFAULT_CELL, true, isStopped), rowIndex, 2);
                table.add(new StatisticLabelCell(Util.getFormatted(String.valueOf(round(flowStatPoint.getTbsL1())), true, "b/s"), secondHeaderWidth, false, CellType.DEFAULT_CELL, true, isStopped), rowIndex, 3);
                table.add(new StatisticLabelCell(Util.getFormatted(String.valueOf(round(flowStatPoint.getRps())), true, "pkt/s"), secondHeaderWidth, true, CellType.DEFAULT_CELL, true, isStopped), rowIndex, 4);
                table.add(new StatisticLabelCell(Util.getFormatted(String.valueOf(round(flowStatPoint.getRbsL2())), true, "b/s"), secondHeaderWidth, false, CellType.DEFAULT_CELL, true, isStopped), rowIndex, 5);
                table.add(new StatisticLabelCell(Util.getFormatted(String.valueOf(round(flowStatPoint.getRbsL1())), true, "b/s"), secondHeaderWidth, true, CellType.DEFAULT_CELL, true, isStopped), rowIndex, 6);
                table.add(new StatisticLabelCell(Util.getFormatted(String.valueOf(tp), true, "pkts"), secondHeaderWidth, false, CellType.DEFAULT_CELL, true, isStopped), rowIndex, 7);
                table.add(new StatisticLabelCell(Util.getFormatted(String.valueOf(rp), true, "pkts"), secondHeaderWidth, true, CellType.DEFAULT_CELL, true, isStopped), rowIndex, 8);
                table.add(new StatisticLabelCell(Util.getFormatted(String.valueOf(tb), true, "B"), secondHeaderWidth, false, CellType.DEFAULT_CELL, true, isStopped), rowIndex, 9);
                table.add(new StatisticLabelCell(Util.getFormatted(String.valueOf(rb), true, "B"), secondHeaderWidth, true, CellType.DEFAULT_CELL, true, isStopped), rowIndex, 10);

                rowIndex++;
            }
        }
    }

    static double round(double value) {
        return ((long)(value*100))/100.0;
    }
}
