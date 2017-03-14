package com.exalttech.trex.ui.controllers.ports.tabs;

import com.exalttech.trex.util.Initialization;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;

public class PortAttributes extends BorderPane {
    @FXML
    private BorderPane root;
    
    public PortAttributes() {
        Initialization.initializeFXML(this, "/fxml/ports/PortAttributes.fxml");
    }
}
