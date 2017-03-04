package com.exalttech.trex.ui.controllers.Dashboard;

import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.WindowEvent;
import javafx.scene.control.ScrollPane;
import javafx.util.Duration;

import jfxtras.labs.scene.control.gauge.linear.SimpleMetroArcGauge;
import jfxtras.labs.scene.control.gauge.linear.elements.PercentSegment;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import com.exalttech.trex.ui.PortsManager;
import com.exalttech.trex.ui.views.services.RefreshingService;
import com.exalttech.trex.ui.views.DashboardGlobalStatisticsGauge;
import com.exalttech.trex.ui.views.DashboardGlobalStatisticsPanel;
import com.exalttech.trex.ui.views.statistics.StatsLoader;
import com.exalttech.trex.util.Constants;
import com.exalttech.trex.util.Util;


public class DashboardGlobalStatistics implements Initializable {
    private static final Logger LOG = Logger.getLogger(DashboardGlobalStatistics.class.getName());

    @FXML
    private ScrollPane root;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        refreshingService = new RefreshingService();
        refreshingService.setPeriod(Duration.seconds(Constants.REFRESH_ONE_INTERVAL_SECONDS));
        refreshingService.setOnSucceeded(this::onRefreshSucceeded);
        refreshingService.start();

        portManager = PortsManager.getInstance();

        // TODO: find another way to adding "WINDOW_CLOSE_REQUEST" event listener
        // This ugly solution is used because we have no scene and no window here
        root.sceneProperty().addListener(((observable, oldValue, newValue) -> {
            if (oldValue == null && newValue != null) {
                newValue.windowProperty().addListener((observableWindow, oldWindow, newWindow) -> {
                    if (oldWindow == null && newWindow != null) {
                        newWindow.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, this::onWindowCloseRequest);
                    }
                });
            }
        }));
    }

    private void onRefreshSucceeded(WorkerStateEvent event) {
        Map<String, String> currentStatsList = StatsLoader.getInstance().getLoadedStatsList();
        Map<String, String> currentFlowStatsMap = StatsLoader.getInstance().getLoadedFlowStatsMap();

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
        totalStream.setValue(String.valueOf(currentFlowStatsMap.keySet().size() - 1));
        activePort.setValue(portManager.getActivePort());
        dropRate.setValue(Util.getFormatted(currentStatsList.get("m_rx_drop_bps"), true, "b/sec"));
        queueFull.setValue(Util.getFormatted(queue, true, "pkts"));
    }

    private void onWindowCloseRequest(WindowEvent window) {
        if (refreshingService.isRunning()) {
            refreshingService.cancel();
        }
    }

    private static void updateGauge(SimpleMetroArcGauge gauge, String data) {
        if (Util.isNullOrEmpty(data)) {
            data = "0";
        }
        Double value = Double.parseDouble(data);

        gauge.segments().clear();

        gauge.getStyleClass().removeAll("colorscheme-red-to-grey-2", "colorscheme-green-to-grey-2");
        if (value >= 90) {
            gauge.getStyleClass().add("colorscheme-red-to-grey-2");
        } else {
            gauge.getStyleClass().add("colorscheme-green-to-grey-2");
        }

        gauge.setValue(value / 100);
        gauge.segments().add(new PercentSegment(gauge, 0.0, value));
        gauge.segments().add(new PercentSegment(gauge, value, 100.0));
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
