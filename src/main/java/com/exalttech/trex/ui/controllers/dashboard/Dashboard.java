package com.exalttech.trex.ui.controllers.dashboard;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import com.exalttech.trex.ui.controllers.dashboard.filters.DashboardPortsSelector;
import com.exalttech.trex.ui.controllers.dashboard.filters.DashboardStreamsSelector;
import com.exalttech.trex.ui.controllers.dashboard.tabs.charts.DashboardTabCharts;
import com.exalttech.trex.ui.controllers.dashboard.tabs.latency.DashboardTabLatency;
import com.exalttech.trex.ui.controllers.dashboard.tabs.ports.DashboardTabPorts;
import com.exalttech.trex.ui.controllers.dashboard.tabs.streams.DashboardTabStreams;
import com.exalttech.trex.ui.dialog.DialogView;
import com.exalttech.trex.ui.views.services.RefreshingService;
import com.exalttech.trex.ui.views.statistics.LatencyStatsLoader;
import com.exalttech.trex.ui.views.statistics.StatsLoader;
import com.exalttech.trex.util.Constants;
import com.exalttech.trex.util.Initialization;


public class Dashboard extends DialogView implements Initializable {
    private static final String PORTS_TAB_LABEL = "Ports";

    @FXML
    private BorderPane root;
    @FXML
    private DashboardPortsSelector portsSelector;
    @FXML
    private DashboardStreamsSelector streamsSelector;
    @FXML
    private TabPane tabPane;
    @FXML
    private DashboardTabPorts ports;
    @FXML
    private DashboardTabStreams streams;
    @FXML
    private DashboardTabLatency latency;
    @FXML
    private DashboardTabCharts charts;
    @FXML
    private Button clearButton;

    RefreshingService refreshingService;

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
        LatencyStatsLoader.getInstance().reset();
        handleUpdate(event);
    }

    private void handleUpdate(final Event event) {
        final String selectedTab = tabPane.getSelectionModel().getSelectedItem().getText();
        if (selectedTab.equals("Ports")) {
            ports.update(portsSelector.getSelectedPortIndexes());
        }
        final Map<Integer, String> selectedPGIds = streamsSelector.getSelectedPGIds();
        switch (selectedTab) {
            case "Streams":
                streams.update(selectedPGIds);
                break;
            case "Latency":
                latency.update(selectedPGIds);
                break;
            case "Charts":
                charts.update(selectedPGIds);
                break;
        }
    }

    private void onWindowCloseRequest(WindowEvent window) {
        if (refreshingService.isRunning()) {
            refreshingService.cancel();
        }
        ports.reset();
    }
}
