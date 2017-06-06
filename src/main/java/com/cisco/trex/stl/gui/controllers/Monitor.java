package com.cisco.trex.stl.gui.controllers;

import com.exalttech.trex.util.Initialization;
import javafx.scene.layout.AnchorPane;

public class Monitor extends AnchorPane {
    public Monitor() {
        Initialization.initializeFXML(this, "/fxml/pkt_capture/Monitor.fxml");
    }
}
