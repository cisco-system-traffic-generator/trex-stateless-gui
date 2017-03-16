package com.exalttech.trex.ui.controllers.dashboard.tabs.latency;

import com.exalttech.trex.ui.models.stats.flow.StatsFlowStream;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.exalttech.trex.ui.models.stats.latency.StatsLatencyStream;
import com.exalttech.trex.ui.models.stats.latency.StatsLatencyStreamErrCntrs;
import com.exalttech.trex.ui.models.stats.latency.StatsLatencyStreamLatency;
import com.exalttech.trex.ui.models.stats.latency.StatsLatencyTableRow;
import com.exalttech.trex.ui.views.statistics.StatsLoader;
import com.exalttech.trex.util.ArrayHistory;
import com.exalttech.trex.util.Initialization;


public class DashboardTabLatency extends AnchorPane {
    @FXML
    private TableView table;

    public DashboardTabLatency() {
        Initialization.initializeFXML(this, "/fxml/Dashboard/tabs/latency/DashboardTabLatency.fxml");
    }

    public void update(Set<Integer> visiblePorts, Set<String> visibleStreams) {
        if (visibleStreams != null && visibleStreams.isEmpty()) {
            table.getItems().clear();
            return;
        }

        StatsLatencyTableRow selectedRow = ((StatsLatencyTableRow) table.getSelectionModel().getSelectedItem());
        String selectedStream = selectedRow != null ? selectedRow.getPgId() : null;

        Map<String, StatsLatencyStream> latencyStatsByStreams = StatsLoader.getInstance().getLatencyStatsMap();
        Map<String, ArrayHistory<StatsFlowStream>> flowStatsMap = StatsLoader.getInstance().getFlowStatsHistoryMap();

        Map<String, Integer> streamIndexes = buildStreamsIndexesMap();
        Set<String> visitedStreams = new HashSet<>();
        latencyStatsByStreams.forEach((String stream, StatsLatencyStream latencyStats) -> {
            visitedStreams.add(stream);

            if (visibleStreams != null && !visibleStreams.contains(stream)) {
                return;
            }

            if (latencyStats == null) {
                return;
            }

            StatsLatencyStreamLatency latency = latencyStats.getLatency();
            if (latency == null) {
                return;
            }

            StatsLatencyStreamErrCntrs errCntrs = latencyStats.getErrCntrs();
            if (errCntrs == null) {
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

            StatsLatencyTableRow row = new StatsLatencyTableRow();
            row.setPgId(stream);
            row.setTxPkts(flowStats.calcTotalTxPkts(visiblePorts));
            row.setRxPkts(flowStats.calcTotalRxPkts(visiblePorts));
            row.setMaxLatency(latency.getTotalMax());
            row.setAvgLatency(((int)(latency.getAverage()*100.0))/100.0);
            row.setJitter(latency.getJitter());
            row.setDup(errCntrs.getDup());
            row.setDropped(errCntrs.getDropped());
            row.setOutOfOrder(errCntrs.getOutOfOrder());
            row.setSeqToHigh(errCntrs.getSeqTooHigh());
            row.setSeqToLow(errCntrs.getSeqTooLow());

            Integer streamIndex = streamIndexes.get(stream);
            if (streamIndex == null) {
                table.getItems().add(row);
            } else {
                table.getItems().set(streamIndex, row);
            }
        });

        table.getItems().removeIf((Object row) -> !visitedStreams.contains(((StatsLatencyTableRow) row).getPgId()));

        selectStream(selectedStream);
    }

    private Map<String, Integer> buildStreamsIndexesMap() {
        Map<String, Integer> streamIndexes = new HashMap<>();
        int rowsCount = table.getItems().size();
        for (int i = 0; i < rowsCount; ++i) {
            StatsLatencyTableRow row = (StatsLatencyTableRow) table.getItems().get(i);
            streamIndexes.put(row.getPgId(), i);
        }
        return streamIndexes;
    }

    private void selectStream(String stream) {
        if (stream == null) {
            return;
        }
        int rowsCount = table.getItems().size();
        for (int i = 0; i < rowsCount; ++i) {
            StatsLatencyTableRow row = (StatsLatencyTableRow) table.getItems().get(i);
            if (row.getPgId().equals(stream)) {
                table.getSelectionModel().select(i);
                break;
            }
        }
    }
}
