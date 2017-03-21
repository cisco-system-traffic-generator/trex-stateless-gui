package com.exalttech.trex.ui.controllers.dashboard;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.net.URL;
import java.util.HashSet;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import com.exalttech.trex.ui.controllers.dashboard.filters.DashboardPortsFilter;
import com.exalttech.trex.ui.controllers.dashboard.tabs.charts.DashboardTabCharts;
import com.exalttech.trex.ui.controllers.dashboard.tabs.latency.DashboardTabLatency;
import com.exalttech.trex.ui.controllers.dashboard.tabs.ports.DashboardTabPorts;
import com.exalttech.trex.ui.controllers.dashboard.tabs.streams.DashboardTabStreams;
import com.exalttech.trex.ui.dialog.DialogView;
import com.exalttech.trex.ui.models.stats.flow.StatsFlowStream;
import com.exalttech.trex.ui.views.services.RefreshingService;
import com.exalttech.trex.ui.views.statistics.StatsLoader;
import com.exalttech.trex.util.ArrayHistory;
import com.exalttech.trex.util.Constants;
import com.exalttech.trex.util.Initialization;


public class Dashboard extends DialogView implements Initializable {
    @FXML
    private BorderPane root;
    @FXML
    private DashboardPortsFilter portsFilter;
    @FXML
    private DashboardTabPorts ports;
    @FXML
    private DashboardTabStreams streams;
    @FXML
    private DashboardTabLatency latency;
    @FXML
    private DashboardTabCharts charts;

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
    public void onEnterKeyPressed(Stage stage) {
        // Nothing to do
    }

    @FXML
    public void handleUpdate(Event event) {
        Set<Integer> visiblePorts = portsFilter.getSelectedPortIndexes();
        Set<String> visibleStreams = getVisibleStream(visiblePorts);
        ports.update(visiblePorts);
        streams.update(visiblePorts, visibleStreams);
        latency.update(visiblePorts, visibleStreams);
        charts.update(visiblePorts, visibleStreams);
    }

    private void onWindowCloseRequest(WindowEvent window) {
        if (refreshingService.isRunning()) {
            refreshingService.cancel();
        }
        ports.reset();
    }

    private static Set<String> getVisibleStream(Set<Integer> visiblePorts) {
        if (visiblePorts == null) {
            return null;
        }

        final Set<String> visibleStreams = new HashSet<>();

        if (visiblePorts.isEmpty()) {
            return visibleStreams;
        }

        final Map<String, ArrayHistory<StatsFlowStream>> flowStats = StatsLoader.getInstance().getFlowStatsHistoryMap();

        flowStats.forEach((String stream, ArrayHistory<StatsFlowStream> streamHistory) -> {
            final StatsFlowStream last = streamHistory.last();
            if (last == null) {
                return;
            }

            if (
                    visiblePorts.stream().anyMatch(last.getTxPkts()::containsKey)
                    || visiblePorts.stream().anyMatch(last.getTxBytes()::containsKey)
                    || visiblePorts.stream().anyMatch(last.getRxPkts()::containsKey)
                    || visiblePorts.stream().anyMatch(last.getRxBytes()::containsKey)
            ) {
                visibleStreams.add(stream);
            }
        });

        return visibleStreams;
    }
}
