package com.exalttech.trex.ui.controllers.dashboard;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import java.util.HashSet;
import java.util.Set;

import com.exalttech.trex.ui.controllers.dashboard.tabs.latency.DashboardTabLatency;
import com.exalttech.trex.ui.controllers.dashboard.tabs.ports.DashboardTabPorts;
import com.exalttech.trex.ui.controllers.dashboard.tabs.streams.DashboardTabStreams;
import com.exalttech.trex.ui.dialog.DialogView;
import com.exalttech.trex.ui.PortsManager;


public class Dashboard extends DialogView {
    @FXML
    private ComboBox<String> portFilterSelector;
    @FXML
    private DashboardTabPorts ports;
    @FXML
    private DashboardTabStreams streams;
    @FXML
    private DashboardTabLatency latency;

    public void handlePortFilterSelection(Event event) {
        String val = portFilterSelector.getSelectionModel().getSelectedItem();
        Set<Integer> visiblePorts = !val.equals("All") ?
            new HashSet<Integer>(PortsManager.getInstance().getOwnedPortIndexes()) :
            null;
        ports.setVisiblePorts(visiblePorts);
        streams.setVisiblePorts(visiblePorts);
        latency.setVisiblePorts(visiblePorts);
    }

    @Override
    public void onEnterKeyPressed(Stage stage) {
        // Nothing to do
    }
}
