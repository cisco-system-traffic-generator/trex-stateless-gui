package com.exalttech.trex.ui.controllers.dashboard.tabs.ports;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.exalttech.trex.ui.views.statistics.StatsLoader;
import com.exalttech.trex.ui.views.statistics.StatsTableGenerator;
import com.exalttech.trex.ui.PortsManager;
import com.exalttech.trex.util.Initialization;
import com.exalttech.trex.util.Util;


public class DashboardTabPorts extends AnchorPane {
    @FXML
    private ScrollPane statTableContainer;

    StatsTableGenerator statsTableGenerator;
    PortsManager portManager;
    Map<String, String> currentStatsList = new HashMap<>();
    Map<String, String> cachedStatsList = new HashMap<>();
    private Set<Integer> lastVisiblePorts;

    public DashboardTabPorts() {
        Initialization.initializeFXML(this, "/fxml/Dashboard/tabs/ports/DashboardTabPorts.fxml");

        statsTableGenerator = new StatsTableGenerator();
        portManager = PortsManager.getInstance();

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
    }

    public void update(Set<Integer> visiblePorts) {
        this.lastVisiblePorts = visiblePorts;
        currentStatsList = StatsLoader.getInstance().getLoadedStatsList();
        String data = currentStatsList.get("m_cpu_util");
        if (Util.isNullOrEmpty(data)) {
            data = "0";
        }

        buildPortStatTable();
    }

    public void reset() {
        statsTableGenerator.reset();
    }

    private void buildPortStatTable() {
        double colWidth = (statTableContainer.getWidth() - 170) / (portManager.getPortCount(false) + 1);
        if (colWidth < 150) {
            colWidth = 150;
        }
        statTableContainer.setContent(
                statsTableGenerator.getPortStatTable(
                        cachedStatsList,
                        portManager.getPortList().size(),
                        true,
                        colWidth,
                        lastVisiblePorts
                )
        );
    }
}
