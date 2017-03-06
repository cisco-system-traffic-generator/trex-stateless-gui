package com.exalttech.trex.ui.views;

import com.exalttech.trex.util.Initialization;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;


public class DashboardTabStreams extends AnchorPane {
    public DashboardTabStreams() {
        Initialization.initializeFXML(this, "/fxml/Dashboard/DashboardTabStreams.fxml");
    }
}
