package com.exalttech.trex.ui.controllers.dashboard.tabs.latency;

import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.util.*;

import com.exalttech.trex.ui.models.json.stats.latency.JSONStatsErrCntrs;
import com.exalttech.trex.ui.models.json.stats.latency.JSONStatsLatency;
import com.exalttech.trex.ui.models.json.stats.latency.JSONStatsStream;
import com.exalttech.trex.ui.models.json.stats.streams.JSONFlowStatsStream;
import com.exalttech.trex.ui.models.stats.latency.StatsLatencyTableRow;
import com.exalttech.trex.ui.views.services.RefreshingService;
import com.exalttech.trex.ui.views.statistics.StatsLoader;
import com.exalttech.trex.util.Constants;
import com.exalttech.trex.util.Initialization;
import com.exalttech.trex.util.StatsUtils;
import com.exalttech.trex.util.Util;


public class DashboardTabLatencyTable extends AnchorPane {
    @FXML
    private AnchorPane root;
    @FXML
    private TableView table;

    private RefreshingService refreshingService;
    private Set<Integer> visiblePorts;

    public DashboardTabLatencyTable() {
        Initialization.initializeFXML(this, "/fxml/Dashboard/tabs/latency/DashboardTabLatencyTable.fxml");

        refreshingService = new RefreshingService();
        refreshingService.setPeriod(Duration.seconds(Constants.REFRESH_ONE_INTERVAL_SECONDS));
        refreshingService.setOnSucceeded(this::onRefreshSucceeded);
        refreshingService.start();

        Initialization.initializeCloseEvent(root, this::onWindowCloseRequest);
    }

    public void setVisiblePorts(Set<Integer> visiblePorts) {
        this.visiblePorts = visiblePorts;
    }

    private void onRefreshSucceeded(WorkerStateEvent event) {
        Set<String> visibleStreams = StatsUtils.getVisibleStream(visiblePorts);
        if (visibleStreams != null && visibleStreams.isEmpty()) {
            table.getItems().clear();
            return;
        }

        StatsLatencyTableRow selectedRow = ((StatsLatencyTableRow) table.getSelectionModel().getSelectedItem());
        String selectedStream = selectedRow != null ? selectedRow.getPgId() : null;

        Map<String, String> latencyStatsByStreams = StatsLoader.getInstance().getLatencyStatsMap();
        Map<String, String> flowStatsMap = StatsLoader.getInstance().getLoadedFlowStatsMap();

        Map<String, Integer> streamIndexes = buildStreamsIndexesMap();
        Set<String> visitedStreams = new HashSet<String>();
        latencyStatsByStreams.forEach((String stream, String jsonLatencyStats) -> {
            visitedStreams.add(stream);

            if (visibleStreams != null && !visibleStreams.contains(stream)) {
                return;
            }

            JSONStatsStream latencyStats = (JSONStatsStream) Util.fromJSONString(
                    jsonLatencyStats,
                    JSONStatsStream.class
            );
            if (latencyStats == null) {
                return;
            }

            JSONStatsLatency latency = latencyStats.getLatency();
            if (latency == null) {
                return;
            }

            JSONStatsErrCntrs errCntrs = latencyStats.getErr_cntrs();
            if (errCntrs == null) {
                return;
            }

            String jsonFlowStats = flowStatsMap.get(stream);
            if (jsonFlowStats == null) {
                return;
            }

            JSONFlowStatsStream flowStats = (JSONFlowStatsStream) Util.fromJSONString(
                    jsonFlowStats,
                    JSONFlowStatsStream.class
            );
            if (flowStats == null) {
                return;
            }

            StatsLatencyTableRow row = new StatsLatencyTableRow();
            row.setPgId(stream);
            row.setTxPkts(calcTotalValue(flowStats.getTx_pkts()));
            row.setRxPkts(calcTotalValue(flowStats.getRx_pkts()));
            row.setMaxLatency(latency.getTotal_max());
            row.setAvgLatency(((int)(latency.getAverage()*100.0))/100.0);
            row.setJitter(latency.getJitter());
            row.setDup(errCntrs.getDup());
            row.setDropped(errCntrs.getDropped());
            row.setOutOfOrder(errCntrs.getOut_of_order());
            row.setSeqToHigh(errCntrs.getSeq_too_high());
            row.setSeqToLow(errCntrs.getSeq_too_low());

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

    private void onWindowCloseRequest(WindowEvent window) {
        if (refreshingService.isRunning()) {
            refreshingService.cancel();
        }
    }

    private Map<String, Integer> buildStreamsIndexesMap() {
        Map<String, Integer> streamIndexes = new HashMap<String, Integer>();
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

    private static long calcTotalValue(Map<Integer, Long> valuesByPorts) {
        if (valuesByPorts == null) {
            return 0;
        }
        long total = 0;
        for (long value : valuesByPorts.values()) {
            total += value;
        }
        return total;
    }
}
