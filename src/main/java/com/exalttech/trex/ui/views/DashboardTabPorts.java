package com.exalttech.trex.ui.views;

import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ScrollPane;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import com.exalttech.trex.ui.views.services.RefreshingService;
import com.exalttech.trex.ui.views.statistics.StatsTableGenerator;
import com.exalttech.trex.util.Constants;
import com.exalttech.trex.util.Initialization;


public class DashboardTabPorts extends ScrollPane {
    @FXML
    ScrollPane root;

    StatsTableGenerator statsTableGenerator;
    RefreshingService readingStatService;

    public DashboardTabPorts() {
        Initialization.initializeFXML(this, "/fxml/Dashboard/DashboardTabPorts.fxml");

        statsTableGenerator = new StatsTableGenerator();
        readingStatService = new RefreshingService();
        readingStatService.setPeriod(Duration.seconds(Constants.REFRESH_ONE_INTERVAL_SECONDS));
        readingStatService.setOnSucceeded(this::onRefreshSucceeded);
        readingStatService.start();

        Initialization.initializeCloseEvent(root, this::onWindowCloseRequest);
    }

    private void onRefreshSucceeded(WorkerStateEvent event) {
    }

    private void onWindowCloseRequest(WindowEvent window) {
        if (readingStatService.isRunning()) {
            readingStatService.cancel();
        }
        statsTableGenerator.reset();
    }
}
