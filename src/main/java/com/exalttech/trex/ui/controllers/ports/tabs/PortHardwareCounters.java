package com.exalttech.trex.ui.controllers.ports.tabs;

import com.exalttech.trex.core.ConnectionManager;
import com.exalttech.trex.ui.PortsManager;
import com.exalttech.trex.ui.models.Port;
import com.exalttech.trex.ui.models.PortModel;
import com.exalttech.trex.ui.views.services.RefreshingService;
import com.exalttech.trex.ui.views.statistics.StatsTableGenerator;
import com.exalttech.trex.util.Initialization;
import com.exalttech.trex.util.Util;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import org.controlsfx.control.textfield.CustomTextField;

import java.util.Map;

public class PortHardwareCounters extends BorderPane {

    @FXML
    ScrollPane statXTableContainer;
    @FXML
    AnchorPane statXTableWrapper;
    @FXML
    CheckBox statXTableNotEmpty;
    @FXML
    CustomTextField statXTableFilter;
    @FXML
    Button resetCounters;
    
    private boolean resetCountersRequested = false;
    
    private RefreshingService refreshingService;

    private Port port;

    private final StatsTableGenerator statsTableGenerator;
    
    private PortsManager portManager;
    
    public PortHardwareCounters() {
        Initialization.initializeFXML(this, "/fxml/ports/PortHardwareCounters.fxml");
        portManager = PortsManager.getInstance();
        statsTableGenerator = new StatsTableGenerator();
        
        refreshingService = new RefreshingService();
        refreshingService.setPeriod(Duration.seconds(0.5));
        refreshingService.setOnSucceeded(e -> update());
        
        resetCounters.setOnAction(e -> resetCountersRequested = true);
    }
    
    
    public void bindModel(PortModel model, boolean runPolling) {
        if (refreshingService.isRunning()) {
            refreshingService.cancel();
        }
        port = portManager.getPortList().get(model.getIndex());

        if (runPolling) {
            startPolling();
        }
    }
    
    private void update() {
        try {
            String xStatsNames = ConnectionManager.getInstance().sendPortXStatsNamesRequest(port);
            String xStatsValues = ConnectionManager.getInstance().sendPortXStatsValuesRequest(port);
            Map<String, Long> loadedXStatsList = Util.getXStatsFromJSONString(xStatsNames, xStatsValues);
            port.setXstats(loadedXStatsList);
            Pane pane = statsTableGenerator.generateXStatPane(true, port, statXTableNotEmpty.isSelected(), statXTableFilter.getText(), resetCountersRequested);
            statXTableContainer.setContent(pane);
            statXTableContainer.setVisible(true);
            if (resetCountersRequested) {
                resetCountersRequested = false;
            }
        } catch (Exception ignored) {
        }
    }
    
    public void startPolling() {
        update();
        refreshingService.restart();
    }

    public void stopPolling() {
        refreshingService.cancel();
    } 
}
