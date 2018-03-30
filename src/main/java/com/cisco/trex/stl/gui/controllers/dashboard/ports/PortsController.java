package com.cisco.trex.stl.gui.controllers.dashboard.ports;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.WindowEvent;

import java.util.Map;

import com.cisco.trex.stl.gui.controllers.dashboard.GlobalStatsBaseController;
import com.cisco.trex.stl.gui.controllers.dashboard.selectors.ports.PortsSelectorController;

import com.exalttech.trex.ui.views.statistics.StatsLoader;
import com.exalttech.trex.ui.views.statistics.StatsTableGenerator;
import com.exalttech.trex.ui.PortsManager;
import com.exalttech.trex.util.Initialization;


public class PortsController extends GlobalStatsBaseController {
    @FXML
    private AnchorPane root;
    @FXML
    private PortsSelectorController portsSelector;
    @FXML
    private Pane statTableContainer;

    private StatsTableGenerator statsTableGenerator = new StatsTableGenerator();

    public PortsController() {
        Initialization.initializeFXML(this, "/fxml/dashboard/ports/Ports.fxml");
        Initialization.initializeCloseEvent(root, this::onWindowCloseRequest);
    }

    public void handleUpdate(final Event event) {
        render();
    }

    private void onWindowCloseRequest(WindowEvent window) {
        setActive(false);
        statsTableGenerator.reset();
    }

    @Override
    protected void render() {
        final Map<String, String> currentStatsList = StatsLoader.getInstance().getLoadedStatsList();
        if (currentStatsList == null) {
            return;
        }

        synchronized (currentStatsList) {
            buildPortStatTable();
        }
    }

    private void buildPortStatTable() {
        final PortsManager portsManager = PortsManager.getInstance();

        statTableContainer.getChildren().clear();
        statTableContainer.getChildren().add(
                statsTableGenerator.getPortStatTable(
                        StatsLoader.getInstance().getShadowStatsList(),
                        portsManager.getPortIndexes(),
                        true,
                        150,
                        portsSelector.getSelectedPortIndexes()
                )
        );
    }
}
