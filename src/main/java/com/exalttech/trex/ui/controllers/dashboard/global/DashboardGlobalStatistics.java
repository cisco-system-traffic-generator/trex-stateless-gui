package com.exalttech.trex.ui.controllers.dashboard.global;

import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.util.Map;

import org.apache.log4j.Logger;

import com.exalttech.trex.ui.PortsManager;
import com.exalttech.trex.ui.views.services.RefreshingService;
import com.exalttech.trex.ui.views.statistics.StatsLoader;
import com.exalttech.trex.util.Constants;
import com.exalttech.trex.util.Initialization;
import com.exalttech.trex.util.Util;


public class DashboardGlobalStatistics extends GridPane {
    private static final Logger LOG = Logger.getLogger(DashboardGlobalStatistics.class.getName());

    @FXML
    private GridPane root;
    @FXML
    private DashboardGlobalStatisticsGauge cpuGauge;
    @FXML
    private DashboardGlobalStatisticsGauge rxCpuGauge;
    @FXML
    private DashboardGlobalStatisticsPanel totalTx;
    @FXML
    private DashboardGlobalStatisticsPanel totalTxL1;
    @FXML
    private DashboardGlobalStatisticsPanel totalRx;
    @FXML
    private DashboardGlobalStatisticsPanel totalPps;
    @FXML
    private DashboardGlobalStatisticsPanel totalStream;
    @FXML
    private DashboardGlobalStatisticsPanel activePort;
    @FXML
    private DashboardGlobalStatisticsPanel dropRate;
    @FXML
    private DashboardGlobalStatisticsPanel queueFull;

    private RefreshingService refreshingService;
    PortsManager portManager;

    public DashboardGlobalStatistics() {
        Initialization.initializeFXML(this, "/fxml/Dashboard/global/DashboardGlobalStatistics.fxml");

        refreshingService = new RefreshingService();
        refreshingService.setPeriod(Duration.seconds(Constants.REFRESH_ONE_INTERVAL_SECONDS));
        refreshingService.setOnSucceeded(this::onRefreshSucceeded);
        refreshingService.start();

        portManager = PortsManager.getInstance();

        Initialization.initializeCloseEvent(root, this::onWindowCloseRequest);
    }

    private void onRefreshSucceeded(WorkerStateEvent event) {
        Map<String, String> currentStatsList = StatsLoader.getInstance().getLoadedStatsList();

        cpuGauge.setData(currentStatsList.get("m_cpu_util"));
        rxCpuGauge.setData(currentStatsList.get("m_rx_cpu_util"));

        double m_tx_bps = Double.parseDouble(currentStatsList.get("m_tx_bps"));
        double m_tx_pps = Double.parseDouble(currentStatsList.get("m_tx_pps"));
        // L1 Tx == "m_tx_bps" + 20 * "m_tx_pps" * 8.0
        double l1_tx_bps = m_tx_bps + m_tx_pps * 20.0 * 8.0;
        String queue = getQueue(currentStatsList);

        totalTx.setValue(Util.getFormatted(String.valueOf(m_tx_bps), true, "b/sec"));
        totalTxL1.setValue(Util.getFormatted(String.valueOf(l1_tx_bps), true, "b/sec"));
        totalRx.setValue(Util.getFormatted(currentStatsList.get("m_rx_bps"), true, "b/sec"));
        totalPps.setValue(Util.getFormatted(String.valueOf(m_tx_pps), true, "pkt/sec"));
        activePort.setValue(portManager.getActivePort());
        dropRate.setValue(Util.getFormatted(currentStatsList.get("m_rx_drop_bps"), true, "b/sec"));
        queueFull.setValue(Util.getFormatted(queue, true, "pkts"));

        final int streamsCount = StatsLoader.getInstance().getFlowStatsHistoryMap().keySet().size();
        totalStream.setValue(String.valueOf(streamsCount));
    }

    private void onWindowCloseRequest(WindowEvent window) {
        if (refreshingService.isRunning()) {
            refreshingService.cancel();
        }
    }

    private static String getQueue(Map<String, String> currentStatsList) {
        try {
            String current = currentStatsList.get("m_total_queue_full");
            return String.valueOf(Double.parseDouble(current));
        } catch (NumberFormatException e) {
            LOG.error("Error calculating queue full value", e);
            return "0";
        }
    }
}
