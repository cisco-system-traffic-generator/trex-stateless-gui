package com.cisco.trex.stl.gui.controllers.dashboard.global;

import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;

import com.cisco.trex.stl.gui.storages.PGIDsStorage;
import com.cisco.trex.stl.gui.storages.StatsStorage;

import com.exalttech.trex.ui.PortsManager;
import com.exalttech.trex.ui.views.services.RefreshingService;
import com.exalttech.trex.ui.views.statistics.StatsLoader;
import com.exalttech.trex.util.Constants;
import com.exalttech.trex.util.Initialization;
import com.exalttech.trex.util.Util;


public class Global extends GridPane {
    private static final Logger LOG = Logger.getLogger(Global.class.getName());

    @FXML
    private GridPane root;
    @FXML
    private GlobalPanel cpu;
    @FXML
    private GlobalPanel rxCpu;
    @FXML
    private GlobalPanel totalTx;
    @FXML
    private GlobalPanel totalTxL1;
    @FXML
    private GlobalPanel totalRx;
    @FXML
    private GlobalPanel totalPps;
    @FXML
    private GlobalPanel totalStream;
    @FXML
    private GlobalPanel activePort;
    @FXML
    private GlobalPanel dropRate;
    @FXML
    private GlobalPanel queueFull;

    private RefreshingService refreshingService;
    PortsManager portManager;

    public Global() {
        Initialization.initializeFXML(this, "/fxml/Dashboard/global/Global.fxml");

        refreshingService = new RefreshingService();
        refreshingService.setPeriod(Duration.seconds(Constants.REFRESH_ONE_INTERVAL_SECONDS));
        refreshingService.setOnSucceeded(this::onRefreshSucceeded);
        refreshingService.start();

        portManager = PortsManager.getInstance();

        Initialization.initializeCloseEvent(root, this::onWindowCloseRequest);
    }

    private void onRefreshSucceeded(WorkerStateEvent event) {
        Map<String, String> currentStatsList = StatsLoader.getInstance().getLoadedStatsList();

        String cpuData = currentStatsList.get("m_cpu_util");
        if (Util.isNullOrEmpty(cpuData)) {
            cpuData = "0";
        }
        cpu.setValue(String.format(Locale.US, "%.2f %%", Double.parseDouble(cpuData)));

        String rxCpuData = currentStatsList.get("m_rx_cpu_util");
        if (Util.isNullOrEmpty(rxCpuData)) {
            rxCpuData = "0";
        }
        rxCpu.setValue(String.format(Locale.US, "%.2f %%", Double.parseDouble(rxCpuData)));

        double m_tx_bps = Double.parseDouble(currentStatsList.get("m_tx_bps"));
        double m_tx_pps = Double.parseDouble(currentStatsList.get("m_tx_pps"));
        // L1 Tx == "m_tx_bps" + 20 * "m_tx_pps" * 8.0
        double l1_tx_bps = m_tx_bps + m_tx_pps * 20.0 * 8.0;
        String queue = getQueue(currentStatsList);

        totalTx.setValue(Util.getFormatted(String.valueOf(m_tx_bps), true, "b/s"));
        totalTxL1.setValue(Util.getFormatted(String.valueOf(l1_tx_bps), true, "b/s"));
        totalRx.setValue(Util.getFormatted(currentStatsList.get("m_rx_bps"), true, "b/s"));
        totalPps.setValue(Util.getFormatted(String.valueOf(m_tx_pps), true, "pkt/s"));
        activePort.setValue(portManager.getActivePort());
        dropRate.setValue(Util.getFormatted(currentStatsList.get("m_rx_drop_bps"), true, "b/s"));
        queueFull.setValue(Util.getFormatted(queue, true, "pkts"));

        final PGIDsStorage pgIdStatsStorage = StatsStorage.getInstance().getPGIDsStorage();
        synchronized (pgIdStatsStorage.getDataLock()) {
            totalStream.setValue(String.valueOf(pgIdStatsStorage.getPgIDs().size()));
        }
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

    static double round(double value) {
        return ((int)(value*100))/100.0;
    }
}
