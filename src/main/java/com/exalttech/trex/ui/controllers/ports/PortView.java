package com.exalttech.trex.ui.controllers.ports;

import com.exalttech.trex.ui.controllers.ports.tabs.PortAttributes;
import com.exalttech.trex.ui.controllers.ports.tabs.PortHardwareCounters;
import com.exalttech.trex.ui.controllers.ports.tabs.PortLayerConfiguration;
import com.exalttech.trex.ui.models.FlowControl;
import com.exalttech.trex.util.Initialization;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import org.controlsfx.control.ToggleSwitch;

public class PortView extends TabPane {

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
    
    
    
    public PortView() {
        Initialization.initializeFXML(this, "/fxml/ports/Port.fxml");
        
    }
}
