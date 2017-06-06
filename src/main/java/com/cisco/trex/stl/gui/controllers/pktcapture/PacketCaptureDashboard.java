package com.cisco.trex.stl.gui.controllers.pktcapture;

import com.exalttech.trex.ui.dialog.DialogView;
import javafx.fxml.FXML;
import javafx.stage.Stage;

public class PacketCaptureDashboard extends DialogView {
    @FXML
    private Monitor monitor;
    
    @FXML
    private Record record;

    public PacketCaptureDashboard() {
        
    }

    @Override
    public void onEnterKeyPressed(Stage stage) {
        // Nothing to do
    }
}
