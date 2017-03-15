package com.exalttech.trex.ui.controllers.ports;

import com.exalttech.trex.util.Initialization;
import javafx.scene.control.TabPane;

public class PortView extends TabPane {
    
    public PortView() {
        Initialization.initializeFXML(this, "/fxml/ports/Port.fxml");
        
    }
}
