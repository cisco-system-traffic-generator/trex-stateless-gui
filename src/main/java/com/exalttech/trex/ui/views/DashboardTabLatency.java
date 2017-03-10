package com.exalttech.trex.ui.views;

import javafx.scene.layout.AnchorPane;

import com.exalttech.trex.util.Initialization;


public class DashboardTabLatency extends AnchorPane {
    public DashboardTabLatency() {
        Initialization.initializeFXML(this, "/fxml/Dashboard/tabs/latency/DashboardTabLatency.fxml");
    }
}
