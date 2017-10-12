package com.cisco.trex.stl.gui.controllers.dashboard;

import com.cisco.trex.stl.gui.controllers.dashboard.charts.ChartsController;
import com.cisco.trex.stl.gui.controllers.dashboard.latency.LatencyController;
import com.cisco.trex.stl.gui.controllers.dashboard.ports.PortsController;
import com.cisco.trex.stl.gui.controllers.dashboard.streams.StreamsController;
import com.cisco.trex.stl.gui.controllers.dashboard.utilization.UtilizationController;
import com.cisco.trex.stl.gui.storages.StatsStorage;
import com.exalttech.trex.ui.PortsManager;
import com.exalttech.trex.ui.dialog.DialogView;
import com.exalttech.trex.ui.models.Port;
import com.exalttech.trex.ui.views.statistics.StatsLoader;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;


public class DashboardController extends DialogView implements Initializable {
    private static final String UTILIZATION_TAB_LABEL = "Utilization";
    private static final String PORTS_TAB_LABEL = "Ports";
    private static final String STREAMS_TAB_LABEL = "Streams";
    private static final String LATENCY_TAB_LABEL = "Latency";
    private static final String CHARTS_TAB_LABEL = "Charts";

    private static final String SERVICE_MODE_ENABLED_LABEL = "Service mode is enabled";

    @FXML
    private TabPane tabPane;
    @FXML
    private UtilizationController utilization;
    @FXML
    private PortsController ports;
    @FXML
    private StreamsController streams;
    @FXML
    private LatencyController latency;
    @FXML
    private ChartsController charts;
    @FXML
    private Label warning;

    ServiceModePollingService pollingSvc;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        pollingSvc = new ServiceModePollingService();
        pollingSvc.setPeriod(Duration.seconds(1));
        pollingSvc.setOnSucceeded(e -> {
            final boolean serviceModeEnabled = (boolean) e.getSource().getValue();
            if (serviceModeEnabled) {
                warning.setText(SERVICE_MODE_ENABLED_LABEL);
                warning.setVisible(true);
            } else {
                warning.setVisible(false);
            }
        });

        pollingSvc.start();
    }

    @Override
    public void shutdown() {
        pollingSvc.cancel();
    }

    @Override
    public void onEnterKeyPressed(final Stage stage) {
        // Nothing to do
    }


    @FXML
    public void handleTabChanged(final Event event) {
        switch (tabPane.getSelectionModel().getSelectedItem().getText()) {
            case UTILIZATION_TAB_LABEL:
                utilization.setActive(true);
                ports.setActive(false);
                streams.setActive(false);
                latency.setActive(false);
                charts.setActive(false);
                break;
            case PORTS_TAB_LABEL:
                if (utilization != null) {
                    utilization.setActive(true);
                }
                if (ports != null) {
                    ports.setActive(true);
                }
                if (streams != null) {
                    streams.setActive(false);
                }
                if (latency != null) {
                    latency.setActive(false);
                }
                if (charts != null) {
                    charts.setActive(false);
                }
                break;
            case STREAMS_TAB_LABEL:
                ports.setActive(false);
                streams.setActive(true);
                latency.setActive(false);
                charts.setActive(false);
                break;
            case LATENCY_TAB_LABEL:
                ports.setActive(false);
                streams.setActive(false);
                latency.setActive(true);
                charts.setActive(false);
                break;
            case CHARTS_TAB_LABEL:
                ports.setActive(false);
                streams.setActive(false);
                latency.setActive(false);
                charts.setActive(true);
                break;
        }
    }

    @FXML
    public void handleClearCacheButtonClicked(final ActionEvent event) {
        StatsLoader.getInstance().reset();
        StatsStorage.getInstance().getPGIDStatsStorage().reset();
    }

    protected class ServiceModePollingService extends ScheduledService<Boolean> {
        PortsManager portsManager = PortsManager.getInstance();

        @Override
        protected Task<Boolean> createTask() {
            return new Task<Boolean>() {
                @Override
                protected Boolean call() throws Exception {
                    return portsManager.getPortList()
                            .stream()
                            .anyMatch(Port::getServiceMode);
                }
            };
        }

    }

}
