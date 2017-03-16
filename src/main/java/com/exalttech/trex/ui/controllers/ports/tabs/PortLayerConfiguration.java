package com.exalttech.trex.ui.controllers.ports.tabs;

import com.exalttech.trex.util.Initialization;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

public class PortLayerConfiguration extends BorderPane {
    @FXML
    private AnchorPane root;

    @FXML
    private ToggleGroup mode;

    @FXML
    private TextField source;
    
    @FXML
    private TextField destination;
    
    @FXML
    private TextField pingDestination;
    
    @FXML
    private Label arpStatus;
    
    public PortLayerConfiguration() {
        Initialization.initializeFXML(this, "/fxml/ports/PortLayerConfiguration.fxml");
    }
}
