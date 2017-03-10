package com.exalttech.trex.ui.controllers.dashboard.tabs.ports;

import com.exalttech.trex.ui.PortsManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;

import com.exalttech.trex.ui.views.services.RefreshingService;
import com.exalttech.trex.ui.views.statistics.StatsLoader;
import com.exalttech.trex.ui.views.statistics.StatsTableGenerator;
import com.exalttech.trex.util.Constants;
import com.exalttech.trex.util.Initialization;
import com.exalttech.trex.util.Util;


public class DashboardTabPorts extends AnchorPane {
    @FXML
    private AnchorPane root;
    @FXML
    private ScrollPane statTableContainer;

    StatsTableGenerator statsTableGenerator;
    RefreshingService readingStatService;
    PortsManager portManager;
    Map<String, String> currentStatsList = new HashMap<>();
    Map<String, String> cachedStatsList = new HashMap<>();

    public DashboardTabPorts() {
        Initialization.initializeFXML(this, "/fxml/Dashboard/tabs/ports/DashboardTabPorts.fxml");

        statsTableGenerator = new StatsTableGenerator();
        portManager = PortsManager.getInstance();
        readingStatService = new RefreshingService();
        readingStatService.setPeriod(Duration.seconds(Constants.REFRESH_ONE_INTERVAL_SECONDS));
        readingStatService.setOnSucceeded(this::onRefreshSucceeded);
        readingStatService.start();

        statTableContainer.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                buildPortStatTable();
            }
        });
        statTableContainer.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                buildPortStatTable();
            }
        });

        Initialization.initializeCloseEvent(root, this::onWindowCloseRequest);
    }

    private void onRefreshSucceeded(WorkerStateEvent event) {
        currentStatsList = StatsLoader.getInstance().getLoadedStatsList();
        String data = currentStatsList.get("m_cpu_util");
        if (Util.isNullOrEmpty(data)) {
            data = "0";
        }

        buildPortStatTable();
    }

    private void onWindowCloseRequest(WindowEvent window) {
        if (readingStatService.isRunning()) {
            readingStatService.cancel();
        }
        statsTableGenerator.reset();
    }

    private void buildPortStatTable() {
        double colWidth = (statTableContainer.getWidth() - 150) / (portManager.getPortCount(false) + 1);
        if (colWidth < 130) {
            colWidth = 150;
        }

        statTableContainer.setContent(statsTableGenerator.getPortStatTable(cachedStatsList, portManager.getPortList().size(), true, colWidth, false));
    }
}
