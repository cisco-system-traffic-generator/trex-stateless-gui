package com.exalttech.trex.ui.controllers.dashboard;

import com.exalttech.trex.ui.views.storages.StatsStorage;
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
import java.util.ResourceBundle;

import com.exalttech.trex.ui.controllers.dashboard.filters.DashboardPortsSelector;
import com.exalttech.trex.ui.controllers.dashboard.filters.DashboardStreamsSelector;
import com.exalttech.trex.ui.controllers.dashboard.tabs.charts.Charts;
import com.exalttech.trex.ui.controllers.dashboard.tabs.latency.Latency;
import com.exalttech.trex.ui.controllers.dashboard.tabs.ports.Ports;
import com.exalttech.trex.ui.controllers.dashboard.tabs.streams.Streams;
import com.exalttech.trex.ui.dialog.DialogView;
import com.exalttech.trex.ui.views.services.RefreshingService;
import com.exalttech.trex.ui.views.statistics.StatsLoader;
import com.exalttech.trex.util.Constants;
import com.exalttech.trex.util.Initialization;


public class Dashboard extends DialogView implements Initializable {
    private static final String PORTS_TAB_LABEL = "Ports";
    private static final String STREAMS_TAB_LABEL = "Streams";
    private static final String LATENCY_TAB_LABEL = "Latency";
    private static final String CHARTS_TAB_LABEL = "Charts";

    @FXML
    private BorderPane root;
    @FXML
    private DashboardPortsSelector portsSelector;
    @FXML
    private DashboardStreamsSelector streamsSelector;
    @FXML
    private TabPane tabPane;
    @FXML
    private Ports ports;
    @FXML
    private Streams streams;
    @FXML
    private Latency latency;
    @FXML
    private Charts charts;
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
