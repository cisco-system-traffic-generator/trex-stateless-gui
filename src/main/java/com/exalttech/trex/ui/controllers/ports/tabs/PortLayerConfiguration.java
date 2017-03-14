package com.exalttech.trex.ui.controllers.ports.tabs;

import com.exalttech.trex.util.Initialization;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

public class PortLayerConfiguration extends BorderPane {
    @FXML
    private AnchorPane root;

    public PortLayerConfiguration() {
        Initialization.initializeFXML(this, "/fxml/ports/PortLayerConfiguration.fxml");
    }
}
