package com.cisco.trex.stl.gui.controllers.dashboard;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

import com.cisco.trex.stl.gui.controllers.dashboard.charts.ChartsController;
import com.cisco.trex.stl.gui.controllers.dashboard.latency.LatencyController;
import com.cisco.trex.stl.gui.controllers.dashboard.ports.PortsController;
import com.cisco.trex.stl.gui.controllers.dashboard.selectors.ports.PortsSelectorController;
import com.cisco.trex.stl.gui.controllers.dashboard.selectors.streams.StreamsSelectorController;
import com.cisco.trex.stl.gui.controllers.dashboard.streams.StreamsController;
import com.cisco.trex.stl.gui.storages.StatsStorage;

import com.exalttech.trex.ui.dialog.DialogView;
import com.exalttech.trex.ui.views.services.RefreshingService;
import com.exalttech.trex.ui.views.statistics.StatsLoader;
import com.exalttech.trex.util.Constants;
import com.exalttech.trex.util.Initialization;


public class DashboardController extends DialogView implements Initializable {
    private static final String PORTS_TAB_LABEL = "Ports";
    private static final String STREAMS_TAB_LABEL = "Streams";
    private static final String LATENCY_TAB_LABEL = "Latency";
    private static final String CHARTS_TAB_LABEL = "Charts";

    @FXML
    private BorderPane root;
    @FXML
    private PortsSelectorController portsSelector;
    @FXML
    private StreamsSelectorController streamsSelector;
    @FXML
    private TabPane tabPane;
    @FXML
    private PortsController ports;
    @FXML
    private StreamsController streams;
    @FXML
    private LatencyController latency;
    @FXML
    private ChartsController charts;

    private RefreshingService refreshingService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        refreshingService = new RefreshingService();
        refreshingService.setPeriod(Duration.seconds(Constants.REFRESH_ONE_INTERVAL_SECONDS));
        refreshingService.setOnSucceeded(this::handleUpdate);
        refreshingService.start();

        Initialization.initializeCloseEvent(root, this::onWindowCloseRequest);
    }

    @Override
    public void onEnterKeyPressed(final Stage stage) {
        // Nothing to do
    }

    @FXML
    public void handleTabChanged(final Event event) {
        switch (tabPane.getSelectionModel().getSelectedItem().getText()) {
            case PORTS_TAB_LABEL:
                portsSelector.setVisible(true);
                streamsSelector.setVisible(false);
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
                portsSelector.setVisible(false);
                streamsSelector.setVisible(true);
                streams.setActive(true);
                latency.setActive(false);
                charts.setActive(false);
                break;
            case LATENCY_TAB_LABEL:
                portsSelector.setVisible(false);
                streamsSelector.setVisible(true);
                streams.setActive(false);
                latency.setActive(true);
                charts.setActive(false);
                break;
            case CHARTS_TAB_LABEL:
                portsSelector.setVisible(false);
                streamsSelector.setVisible(true);
                streams.setActive(false);
                latency.setActive(false);
                charts.setActive(true);
                break;
        }

        final boolean isPortsTab = tabPane.getSelectionModel().getSelectedItem().getText().equals(PORTS_TAB_LABEL);
        portsSelector.setVisible(isPortsTab);
        streamsSelector.setVisible(!isPortsTab);
    }

    @FXML
    public void handleFiltersChanged(final Event event) {
        handleUpdate(event);
    }

    @FXML
    public void handleClearCacheButtonClicked(final ActionEvent event) {
        StatsLoader.getInstance().reset();
        StatsStorage.getInstance().getPGIDStatsStorage().reset();
        handleUpdate(event);
    }

    private void handleUpdate(final Event event) {
        final String selectedTab = tabPane.getSelectionModel().getSelectedItem().getText();
        if (selectedTab.equals(PORTS_TAB_LABEL)) {
            ports.update(portsSelector.getSelectedPortIndexes());
        }
    }

    private void onWindowCloseRequest(WindowEvent window) {
        if (refreshingService.isRunning()) {
            refreshingService.cancel();
        }
        ports.reset();
    }
}
