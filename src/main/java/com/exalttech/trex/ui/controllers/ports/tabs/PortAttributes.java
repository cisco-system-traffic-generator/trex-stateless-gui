package com.exalttech.trex.ui.controllers.ports.tabs;

import com.exalttech.trex.ui.models.FlowControl;
import com.exalttech.trex.util.Initialization;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import org.controlsfx.control.ToggleSwitch;

public class PortAttributes extends BorderPane {
    
    @FXML
    private PortAttributes attributesTab;

    @FXML
    private PortLayerConfiguration layerConfigTab;

    @FXML
    private PortHardwareCounters hardwareCounters;

    @FXML
    Label driver;

    @FXML
    Label rxFilterMode;

    @FXML
    Label multicast;

    @FXML
    Label promiscuousMode;

    @FXML
    Label owner;

    @FXML
    Label speed;

    @FXML
    Label status;

    @FXML
    Label captureStatus;

    @FXML
    ToggleSwitch link;

    @FXML
    ToggleSwitch led;

    @FXML
    ToggleSwitch numaMode;

    @FXML
    Label pciAddress;

    @FXML
    Label gratArp;

    @FXML
    ChoiceBox<FlowControl> flowControl;
    
    public PortAttributes() {
        Initialization.initializeFXML(this, "/fxml/ports/PortAttributes.fxml");
    }
}
