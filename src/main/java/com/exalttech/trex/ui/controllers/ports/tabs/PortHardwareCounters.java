package com.exalttech.trex.ui.controllers.ports.tabs;

import com.exalttech.trex.util.Initialization;
import javafx.scene.layout.BorderPane;

public class PortHardwareCounters extends BorderPane {
    public PortHardwareCounters() {
        Initialization.initializeFXML(this, "/fxml/ports/PortHardwareCounters.fxml");
    }
}
