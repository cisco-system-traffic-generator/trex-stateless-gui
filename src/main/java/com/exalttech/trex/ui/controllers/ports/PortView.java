package com.exalttech.trex.ui.controllers.ports;

import com.exalttech.trex.ui.controllers.ports.tabs.PortAttributes;
import com.exalttech.trex.ui.controllers.ports.tabs.PortHardwareCounters;
import com.exalttech.trex.ui.controllers.ports.tabs.PortLayerConfiguration;
import com.exalttech.trex.ui.models.PortModel;
import com.exalttech.trex.util.Initialization;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;

public class PortView extends TabPane {
    
    @FXML
    PortAttributes portAttributes;
    
    @FXML
    PortLayerConfiguration layerConfig;
    
    @FXML
    PortHardwareCounters hardwareCounters;
    
    public PortView() {
        Initialization.initializeFXML(this, "/fxml/ports/Port.fxml");
        
    }

    public void loadModel(PortModel model) {
        portAttributes.bindModel(model);
    }
}
