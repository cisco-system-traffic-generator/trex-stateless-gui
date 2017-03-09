package com.exalttech.trex.ui.views;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

import com.exalttech.trex.util.Initialization;


public class DashboardTabLatency extends AnchorPane {
    @FXML
    private AnchorPane root;

    public DashboardTabLatency() {
        Initialization.initializeFXML(this, "/fxml/Dashboard/DashboardTabLatency.fxml");
    }
}
