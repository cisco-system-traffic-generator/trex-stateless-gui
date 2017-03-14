package com.exalttech.trex.ui.controllers.ports;

import com.exalttech.trex.ui.controllers.ports.tabs.PortAttributes;
import com.exalttech.trex.ui.controllers.ports.tabs.PortHardwareCounters;
import com.exalttech.trex.ui.controllers.ports.tabs.PortLayerConfiguration;
import com.exalttech.trex.util.Initialization;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;

public class PortController extends TabPane {

    @FXML
    private PortAttributes attributesTab;

    @FXML
    private PortLayerConfiguration layerConfigTab;

    @FXML
    private PortHardwareCounters hardwareCounters;
    
    public PortController() {
        Initialization.initializeFXML(this, "/fxml/ports/Port.fxml");
        
    }
}
